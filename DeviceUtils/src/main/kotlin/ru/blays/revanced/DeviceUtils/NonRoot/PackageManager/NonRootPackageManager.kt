package ru.blays.revanced.DeviceUtils.NonRoot.PackageManager

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.os.Build
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.blays.revanced.DeviceUtils.Interfaces.PackageManagerError
import ru.blays.revanced.DeviceUtils.Interfaces.PackageManagerInterface
import ru.blays.revanced.DeviceUtils.Interfaces.PackageManagerResult
import ru.blays.revanced.DeviceUtils.NonRoot.Services.AppInstallService
import ru.blays.revanced.DeviceUtils.NonRoot.Services.AppUninstallService
import ru.blays.revanced.DeviceUtils.NonRoot.Util.doubleUnionTryCatch
import ru.blays.revanced.DeviceUtils.NonRoot.Util.tripleUnionTryCatch
import ru.blays.revanced.shared.Extensions.intentFor
import ru.blays.revanced.shared.Extensions.isNotNull
import ru.blays.revanced.shared.LogManager.BLog
import java.io.File
import java.io.IOException


class NonRootPackageManager(private val context: Context): PackageManagerInterface {

    @Suppress("DEPRECATION", "LocalVariableName")
    val getPackageInfo: (String) -> PackageInfo
        get() = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val FLAG_NOTHING = PackageManager.PackageInfoFlags.of(0)
            context.packageManager.getPackageInfo(it, FLAG_NOTHING)
        } else {
            val FLAG_NOTHING = 0
            context.packageManager.getPackageInfo(it, FLAG_NOTHING)
        }
    }

    @Suppress("DEPRECATION")
    override suspend fun getVersionCode(packageName: String): PackageManagerResult<Int> = coroutineScope {
        return@coroutineScope try {

            val packageInfo = getPackageInfo(packageName)

            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.and(VERSION_IGNORE_MAJOR).toInt()
            } else {
                packageInfo.versionCode
            }

            PackageManagerResult.Success(versionCode)
        } catch (e: PackageManager.NameNotFoundException) {
            PackageManagerResult.Error(
                error = PackageManagerError.GET_FAILED_PACKAGE_VERSION_CODE,
                message = e.stackTraceToString()
            )
        }
    }

    @SuppressLint("WrongConstant")
    override suspend fun getVersionName(packageName: String): PackageManagerResult<String> = coroutineScope {
        return@coroutineScope try {

            val packageInfo = getPackageInfo(packageName)

            val versionName = packageInfo.versionName

            PackageManagerResult.Success(versionName)
        } catch (e: PackageManager.NameNotFoundException) {
            PackageManagerResult.Error(
                error = PackageManagerError.GET_FAILED_PACKAGE_VERSION_NAME,
                message = e.stackTraceToString()
            )
        }
    }

    override suspend fun getInstallationDir(packageName: String): PackageManagerResult<String> = coroutineScope {
        return@coroutineScope try {

            val packageInfo = getPackageInfo(packageName)

            val installationDir = packageInfo
                .applicationInfo
                .sourceDir

            PackageManagerResult.Success(installationDir)
        } catch (e: PackageManager.NameNotFoundException) {
            PackageManagerResult.Error(
                error = PackageManagerError.GET_FAILED_PACKAGE_DIR,
                message = e.stackTraceToString()
            )
        }
    }

    override suspend fun setInstaller(
        targetPackage: String,
        installerPackage: String
    ): PackageManagerResult<Nothing> {
        return PackageManagerResult.Error(
            error = PackageManagerError.SET_FAILED_INSTALLER,
            message = "Unsupported"
        )
    }

    override suspend fun forceStop(packageName: String): PackageManagerResult<Nothing> = coroutineScope {
        return@coroutineScope PackageManagerResult.Error(
            error = PackageManagerError.APP_FAILED_FORCE_STOP,
            message = "Unsupported"
        )
    }

    override suspend fun installApp(apk: File): PackageManagerResult<Nothing> = coroutineScope {
        return@coroutineScope createInstallationSession {
            writeApkToSession(apk)
        }
    }

    override suspend fun installSplitApp(apks: Array<File>): PackageManagerResult<Nothing> = coroutineScope {
        return@coroutineScope createInstallationSession {
            for (apk in apks) {
                writeApkToSession(apk)
            }
        }
    }

    override suspend fun uninstallApp(packageName: String): PackageManagerResult<Nothing> = coroutineScope {
        val packageInstaller = context.packageManager.packageInstaller
        val intent = context.intentFor<AppUninstallService>()
        val pendingIntent = PendingIntent.getService(
            context,
            0,
            intent,
            intentFlags
        ).intentSender
        packageInstaller.uninstall(packageName, pendingIntent)
        return@coroutineScope PackageManagerResult.Success(null)
    }

    override suspend fun launchApp(packageName: String): PackageManagerResult<Nothing> {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        return if (intent.isNotNull()) {
            context.startActivity(intent)
            PackageManagerResult.Success(null)
        } else PackageManagerResult.Error(
            error = PackageManagerError.LAUNCH_FAILED,
            message = "App launch failed"
        )
    }

    private suspend inline fun createInstallationSession(
        crossinline block: suspend PackageInstaller.Session.() -> Unit
    ): PackageManagerResult<Nothing> = coroutineScope {

        val packageInstaller = context.packageManager.packageInstaller

        val sessionParams = PackageInstaller.SessionParams(
            PackageInstaller.SessionParams.MODE_FULL_INSTALL
        ).apply {
            setInstallReason(PackageManager.INSTALL_REASON_USER)
        }

        val intent = context.intentFor<AppInstallService>()

        val pendingIntent = PendingIntent.getService(
            context,
            0,
            intent,
            intentFlags
        ).intentSender

        val sessionId = tripleUnionTryCatch<IOException, SecurityException, IllegalArgumentException, Int>(
            onCatch = {
                return@coroutineScope PackageManagerResult.Error(
                    error = PackageManagerError.SESSION_FAILED_CREATE,
                    message = it.stackTraceToString()
                )
            }
        ) {
            packageInstaller.createSession(sessionParams)
        }

        val session = doubleUnionTryCatch<IOException, SecurityException, PackageInstaller.Session>(
            onCatch = {
                return@coroutineScope PackageManagerResult.Error(
                    error = PackageManagerError.SESSION_FAILED_CREATE,
                    message = it.stackTraceToString()
                )
            }
        ) {
            packageInstaller.openSession(sessionId)
        }

        try {
            session.use {
                it.block()
                it.commit(pendingIntent)
            }
        } catch (e: IOException) {
            return@coroutineScope PackageManagerResult.Error(
                error = PackageManagerError.SESSION_FAILED_WRITE,
                message = e.stackTraceToString()
            )
        } catch (e: SecurityException) {
            return@coroutineScope PackageManagerResult.Error(
                error = PackageManagerError.SESSION_FAILED_COMMIT,
                message = e.stackTraceToString()
            )
        }

        var job: Job? = null

        var statusCode = -1

        job = launch {
            installerStatusFlow.collect { (id, status) ->
                BLog.w("installerStatusFlow", "status: $status, id: $id")
                if (sessionId == id) {
                    BLog.w("installerStatusFlow", "session id equals: true")
                    statusCode = status
                    job?.cancel()
                    return@collect
                }
            }
        }

        job.join()

        BLog.w("installerStatusFlow", "return result")

        if (statusCode == PackageInstaller.STATUS_SUCCESS) {
            return@coroutineScope PackageManagerResult.Success(null)
        } else {
            return@coroutineScope PackageManagerResult.Error(PackageManagerError.INSTALL_FAILED_UNKNOWN, "")
        }
    }

    private suspend fun PackageInstaller.Session.writeApkToSession(apk: File) = coroutineScope {
        apk.inputStream().use { inputStream ->
            openWrite(apk.name, 0, apk.length()).use { outputStream ->
                inputStream.copyTo(outputStream, byteArraySize)
                fsync(outputStream)
            }
        }
    }

    private val intentFlags: Int
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_MUTABLE
            else
                0
        }

    private companion object {
        const val byteArraySize = 1024 * 1024

        const val VERSION_IGNORE_MAJOR = 0xFFFFFFFF
    }

}

internal val installerStatusFlow: MutableStateFlow<Pair<Int, Int>> = MutableStateFlow(-1 to -1)