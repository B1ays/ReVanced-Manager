package ru.blays.revanced.Services.NonRootService.PackageManager

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.os.Build
import ru.blays.revanced.Services.NonRootService.InstallService.AppInstallService
import ru.blays.revanced.Services.NonRootService.InstallService.AppUninstallService
import ru.blays.revanced.Services.NonRootService.Interfaces.PackageManagerError
import ru.blays.revanced.Services.NonRootService.Interfaces.PackageManagerInterface
import ru.blays.revanced.Services.NonRootService.Interfaces.PackageManagerResult
import ru.blays.revanced.Services.NonRootService.Util.doubleUnionTryCatch
import ru.blays.revanced.Services.NonRootService.Util.tripleUnionTryCatch
import java.io.File
import java.io.IOException

class NonRootPackageManager(private val context: Context): PackageManagerInterface {

    @Suppress("DEPRECATION", "LocalVariableName")
    val getPackageInfo: (String) -> PackageInfo
        get() = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val FLAG_NOTHING_TIRAMISU = PackageManager.PackageInfoFlags.of(0)
            context.packageManager.getPackageInfo(it, FLAG_NOTHING_TIRAMISU)
        } else {
            val FLAG_NOTHING_OLD = 0
            context.packageManager.getPackageInfo(it, FLAG_NOTHING_OLD)
        }
    }

    @Suppress("DEPRECATION")
    override suspend fun getVersionCode(packageName: String): PackageManagerResult<Int> {
        return try {

            val packageInfo = getPackageInfo(packageName)

            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.and(VERSION_IGNORE_MAJOR).toInt()
            } else {
                packageInfo.versionCode
            }

            PackageManagerResult.Success(versionCode)
        } catch (e: android.content.pm.PackageManager.NameNotFoundException) {
            PackageManagerResult.Error(
                error = PackageManagerError.GET_FAILED_PACKAGE_VERSION_CODE,
                message = e.stackTraceToString()
            )
        }
    }

    @SuppressLint("WrongConstant")
    override suspend fun getVersionName(packageName: String): PackageManagerResult<String> {
        return try {

            val packageInfo = getPackageInfo(packageName)

            val versionName = packageInfo.versionName

            PackageManagerResult.Success(versionName)
        } catch (e: android.content.pm.PackageManager.NameNotFoundException) {
            PackageManagerResult.Error(
                error = PackageManagerError.GET_FAILED_PACKAGE_VERSION_NAME,
                message = e.stackTraceToString()
            )
        }
    }

    override suspend fun getInstallationDir(packageName: String): PackageManagerResult<String> {
        return try {

            val packageInfo = getPackageInfo(packageName)

            val installationDir = packageInfo
                .applicationInfo
                .sourceDir

            PackageManagerResult.Success(installationDir)
        } catch (e: android.content.pm.PackageManager.NameNotFoundException) {
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

    override suspend fun forceStop(packageName: String): PackageManagerResult<Nothing> {
        return PackageManagerResult.Error(
            error = PackageManagerError.APP_FAILED_FORCE_STOP,
            message = "Unsupported"
        )
    }

    override suspend fun installApp(apk: File): PackageManagerResult<Nothing> {
        return createInstallationSession {
            writeApkToSession(apk)
        }
    }

    override suspend fun installSplitApp(apks: Array<File>): PackageManagerResult<Nothing> {
        return createInstallationSession {
            for (apk in apks) {
                writeApkToSession(apk)
            }
        }
    }

    override suspend fun uninstallApp(packageName: String): PackageManagerResult<Nothing> {
        val packageInstaller = context.packageManager.packageInstaller
        val pendingIntent = PendingIntent.getService(
            context,
            0,
            Intent(context, AppUninstallService::class.java),
            intentFlags
        ).intentSender
        packageInstaller.uninstall(packageName, pendingIntent)
        return PackageManagerResult.Success(null)
    }

    override suspend fun launchApp(packageName: String): PackageManagerResult<Nothing> {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        return if (intent != null) {
            context.startActivity(intent)
            PackageManagerResult.Success(null)
        } else PackageManagerResult.Error(
            error = PackageManagerError.LAUNCH_FAILED,
            message = "App launch failed"
        )
    }

    private inline fun createInstallationSession(
        block: PackageInstaller.Session.() -> Unit
    ): PackageManagerResult<Nothing> {

        val packageInstaller = context.packageManager.packageInstaller

        val sessionParams = PackageInstaller.SessionParams(
            PackageInstaller.SessionParams.MODE_FULL_INSTALL
        ).apply {
            setInstallReason(android.content.pm.PackageManager.INSTALL_REASON_USER)
        }

        val pendingIntent = PendingIntent.getService(
            context,
            0,
            Intent(context, AppInstallService::class.java),
            intentFlags
        ).intentSender

        val sessionId = tripleUnionTryCatch<IOException, SecurityException, IllegalArgumentException, Int>(
            onCatch = {
                return PackageManagerResult.Error(
                    error = PackageManagerError.SESSION_FAILED_CREATE,
                    message = it.stackTraceToString()
                )
            }
        ) {
            packageInstaller.createSession(sessionParams)
        }

        val session = doubleUnionTryCatch<IOException, SecurityException, PackageInstaller.Session>(
            onCatch = {
                return PackageManagerResult.Error(
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
            return PackageManagerResult.Error(
                error = PackageManagerError.SESSION_FAILED_WRITE,
                message = e.stackTraceToString()
            )
        } catch (e: SecurityException) {
            return PackageManagerResult.Error(
                error = PackageManagerError.SESSION_FAILED_COMMIT,
                message = e.stackTraceToString()
            )
        }

        return PackageManagerResult.Success(null)
    }

    private fun PackageInstaller.Session.writeApkToSession(apk: File) {
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