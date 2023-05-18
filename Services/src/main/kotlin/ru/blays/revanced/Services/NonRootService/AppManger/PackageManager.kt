package com.vanced.manager.repository.manager

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Build
import androidx.annotation.RequiresApi
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.nio.*
import com.vanced.manager.installer.service.AppInstallService
import com.vanced.manager.installer.service.AppUninstallService
import com.vanced.manager.util.SuException
import com.vanced.manager.util.awaitOutputOrThrow
import com.vanced.manager.util.doubleUnionTryCatch
import com.vanced.manager.util.errString
import com.vanced.manager.util.tripleUnionTryCatch
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

interface PackageManager {

    suspend fun getVersionCode(packageName: String): PackageManagerResult<Int>

    suspend fun getVersionName(packageName: String): PackageManagerResult<String>

    suspend fun getInstallationDir(packageName: String): PackageManagerResult<String>

    suspend fun setInstaller(targetPackage: String, installerPackage: String): PackageManagerResult<Nothing>

    suspend fun forceStop(packageName: String): PackageManagerResult<Nothing>

    suspend fun installApp(apk: File): PackageManagerResult<Nothing>

    suspend fun installSplitApp(apks: Array<File>): PackageManagerResult<Nothing>

    suspend fun uninstallApp(packageName: String): PackageManagerResult<Nothing>

    suspend fun launchApp(packageName: String): PackageManagerResult<Nothing>

}

class NonrootPackageManager(
    private val context: Context
) : PackageManager {

    @SuppressLint("WrongConstant")
    @Suppress("DEPRECATION")
    override suspend fun getVersionCode(packageName: String): PackageManagerResult<Int> {
        return try {

            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(packageName, FLAG_NOTHING_TIRAMISU)
            } else {
                context.packageManager.getPackageInfo(packageName, FLAG_NOTHING_OLD)
            }

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

    @Suppress("DEPRECATION")
    @SuppressLint("WrongConstant")
    override suspend fun getVersionName(packageName: String): PackageManagerResult<String> {
        return try {

            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(packageName, FLAG_NOTHING_TIRAMISU)
            } else {
                context.packageManager.getPackageInfo(packageName, FLAG_NOTHING_OLD)
            }

            val versionName = packageInfo.versionName

            PackageManagerResult.Success(versionName)
        } catch (e: android.content.pm.PackageManager.NameNotFoundException) {
            PackageManagerResult.Error(
                error = PackageManagerError.GET_FAILED_PACKAGE_VERSION_NAME,
                message = e.stackTraceToString()
            )
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("WrongConstant")
    override suspend fun getInstallationDir(packageName: String): PackageManagerResult<String> {
        return try {

            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(packageName, FLAG_NOTHING_TIRAMISU)
            } else {
                context.packageManager.getPackageInfo(packageName, FLAG_NOTHING_OLD)
            }

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
        } else PackageManagerResult.Error(error = PackageManagerError.LAUNCH_FAILED, message = "App launch failed")
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

        const val FLAG_NOTHING_OLD = 0

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        val FLAG_NOTHING_TIRAMISU = android.content.pm.PackageManager.PackageInfoFlags.of(0)

        const val VERSION_IGNORE_MAJOR = 0xFFFFFFFF
    }

}

class RootPackageManager : PackageManager {

    override suspend fun getVersionCode(packageName: String): PackageManagerResult<Int> {
        return try {
            val keyword = "versionCode="
            val dumpsys = Shell.cmd("dumpsys package $packageName | grep $keyword").awaitOutputOrThrow()
            val versionCode = dumpsys.removePrefix(keyword).substringAfter("minSdk").toInt()

            PackageManagerResult.Success(versionCode)
        } catch (e: SuException) {
            PackageManagerResult.Error(
                error = PackageManagerError.GET_FAILED_PACKAGE_VERSION_CODE,
                message = e.stderrOut
            )
        } catch (e: NumberFormatException) {
            PackageManagerResult.Error(
                error = PackageManagerError.GET_FAILED_PACKAGE_VERSION_CODE,
                message = e.stackTraceToString()
            )
        }
    }

    override suspend fun getVersionName(packageName: String): PackageManagerResult<String> {
        return try {
            val keyword = "versionName="
            val dumpsys = Shell.cmd("dumpsys package $packageName | grep $keyword").awaitOutputOrThrow()
            val versionName = dumpsys.removePrefix(keyword)

            PackageManagerResult.Success(versionName)
        } catch (e: SuException) {
            PackageManagerResult.Error(
                error = PackageManagerError.GET_FAILED_PACKAGE_VERSION_NAME,
                message = e.stderrOut
            )
        }
    }

    override suspend fun getInstallationDir(packageName: String): PackageManagerResult<String> {
        return try {
            val keyword = "path: "
            val dumpsys = Shell.cmd("dumpsys package $packageName | grep $keyword").awaitOutputOrThrow()
            val installationDir = dumpsys.removePrefix(keyword)

            PackageManagerResult.Success(installationDir)
        } catch (e: SuException) {
            PackageManagerResult.Error(
                error = PackageManagerError.GET_FAILED_PACKAGE_DIR,
                message = e.stderrOut
            )
        }
    }

    override suspend fun setInstaller(
        targetPackage: String,
        installerPackage: String
    ): PackageManagerResult<Nothing> {
        return try {
            Shell.cmd("pm set-installer $targetPackage $installerPackage").awaitOutputOrThrow()

            PackageManagerResult.Success(null)
        } catch (e: SuException) {
            PackageManagerResult.Error(
                error = PackageManagerError.SET_FAILED_INSTALLER,
                message = e.stderrOut
            )
        }
    }

    override suspend fun forceStop(packageName: String): PackageManagerResult<Nothing> {
        return try {
            Shell.cmd("am force-stop $packageName").awaitOutputOrThrow()

            PackageManagerResult.Success(null)
        } catch (e: SuException) {
            PackageManagerResult.Error(
                error = PackageManagerError.APP_FAILED_FORCE_STOP,
                message = e.stderrOut
            )
        }
    }

    override suspend fun installApp(apk: File): PackageManagerResult<Nothing> {
        val apkPath = apk.absolutePath

        val size = apk.length()

        val install = Shell.cmd("cat '$apkPath' | pm install -S $size").exec()

        if (!install.isSuccess) {
            val errString = install.errString
            return PackageManagerResult.Error(getEnumForInstallFailed(errString), errString)
        }

        return PackageManagerResult.Success(null)
    }

    override suspend fun installSplitApp(apks: Array<File>): PackageManagerResult<Nothing> {
        val sessionId = try {
            val installCreate = Shell.cmd("pm install-create -r").awaitOutputOrThrow()

            installCreate.toInt()
        } catch (e: SuException) {
            return PackageManagerResult.Error(
                error = PackageManagerError.SESSION_FAILED_CREATE,
                message = e.stderrOut
            )
        } catch (e: NumberFormatException) {
            return PackageManagerResult.Error(
                error = PackageManagerError.SESSION_INVALID_ID,
                message = e.stackTraceToString()
            )
        }

        for (apk in apks) {
            var tempApk: String? = null
            try {
                tempApk = copyApkToTemp(apk)
                Shell.cmd("pm install-write $sessionId '${apk.name}' '$tempApk'").awaitOutputOrThrow()
            } catch (e: SuException) {
                return PackageManagerResult.Error(
                    error = PackageManagerError.SESSION_FAILED_WRITE,
                    message = e.stderrOut
                )
            } catch (e: IOException) {
                return PackageManagerResult.Error(
                    error = PackageManagerError.SESSION_FAILED_COPY,
                    message = e.stackTraceToString()
                )
            } finally {
                tempApk?.let { deleteTempApk(it) }
            }
        }

        return try {
            Shell.cmd("pm install-commit $sessionId").awaitOutputOrThrow()

            PackageManagerResult.Success(null)
        } catch (e: SuException) {
            PackageManagerResult.Error(
                error = getEnumForInstallFailed(e.stderrOut),
                message = e.stderrOut
            )
        }
    }

    override suspend fun uninstallApp(packageName: String): PackageManagerResult<Nothing> {
        return try {
            Shell.cmd("pm uninstall $packageName").awaitOutputOrThrow()

            PackageManagerResult.Success(null)
        } catch (e: SuException) {
            PackageManagerResult.Error(
                error = PackageManagerError.UNINSTALL_FAILED,
                message = e.stderrOut
            )
        }
    }

    override suspend fun launchApp(packageName: String): PackageManagerResult<Nothing> {
        return try {
            Shell.cmd("monkey -p $packageName -c android.intent.category.LAUNCHER 1")
            PackageManagerResult.Success(null)
        } catch (e: SuException) {
            PackageManagerResult.Error(
                error = PackageManagerError.LAUNCH_FAILED,
                message = e.stderrOut
            )
        }
    }

    @Throws(
        IOException::class,
        FileNotFoundException::class
    )
    private fun copyApkToTemp(apk: File): String? {

        val apkAbsolutePath = apk.absolutePath

        val tmpPath = "/data/local/tmp/'${apk.name}'"

        val copy = Shell.cmd("cp -f '$apkAbsolutePath' '$tmpPath'").exec()

        return if (copy.isSuccess) tmpPath else null
    }

    private fun deleteTempApk(tmpPath: String) {
        Shell.cmd("rm -f '$tmpPath'")
    }

}

enum class PackageManagerError {
    SET_FAILED_INSTALLER,
    GET_FAILED_PACKAGE_DIR,
    GET_FAILED_PACKAGE_VERSION_NAME,
    GET_FAILED_PACKAGE_VERSION_CODE,

    APP_FAILED_FORCE_STOP,

    SESSION_FAILED_CREATE,
    SESSION_FAILED_COMMIT,
    SESSION_FAILED_WRITE,
    SESSION_FAILED_COPY,
    SESSION_FAILED_OPEN,
    SESSION_INVALID_ID,

    INSTALL_FAILED_ABORTED,
    INSTALL_FAILED_ALREADY_EXISTS,
    INSTALL_FAILED_CPU_ABI_INCOMPATIBLE,
    INSTALL_FAILED_INSUFFICIENT_STORAGE,
    INSTALL_FAILED_INVALID_APK,
    INSTALL_FAILED_VERSION_DOWNGRADE,
    INSTALL_FAILED_PARSE_NO_CERTIFICATES,
    INSTALL_FAILED_UNKNOWN,

    UNINSTALL_FAILED,

    LAUNCH_FAILED,

    LINK_FAILED_UNMOUNT,
    LINK_FAILED_MOUNT,

    PATCH_FAILED_COPY,
    PATCH_FAILED_CHMOD,
    PATCH_FAILED_CHOWN,
    PATCH_FAILED_CHCON,
    PATCH_FAILED_DESTROY,

    SCRIPT_FAILED_SETUP_POST_FS,
    SCRIPT_FAILED_SETUP_SERVICE_D,
    SCRIPT_FAILED_DESTROY_POST_FS,
    SCRIPT_FAILED_DESTROY_SERVICE_D,
}

internal fun getEnumForInstallFailed(outString: String): PackageManagerError {
    return when {
        outString.contains("INSTALL_FAILED_ABORTED") -> PackageManagerError.INSTALL_FAILED_ABORTED
        outString.contains("INSTALL_FAILED_ALREADY_EXISTS") -> PackageManagerError.INSTALL_FAILED_ALREADY_EXISTS
        outString.contains("INSTALL_FAILED_CPU_ABI_INCOMPATIBLE") -> PackageManagerError.INSTALL_FAILED_CPU_ABI_INCOMPATIBLE
        outString.contains("INSTALL_FAILED_INSUFFICIENT_STORAGE") -> PackageManagerError.INSTALL_FAILED_INSUFFICIENT_STORAGE
        outString.contains("INSTALL_FAILED_INVALID_APK") -> PackageManagerError.INSTALL_FAILED_INVALID_APK
        outString.contains("INSTALL_FAILED_VERSION_DOWNGRADE") -> PackageManagerError.INSTALL_FAILED_VERSION_DOWNGRADE
        outString.contains("INSTALL_PARSE_FAILED_NO_CERTIFICATES") -> PackageManagerError.INSTALL_FAILED_PARSE_NO_CERTIFICATES
        else -> PackageManagerError.INSTALL_FAILED_UNKNOWN
    }
}

sealed class PackageManagerResult<out V> {
    data class Success<out V>(val value: V?) : PackageManagerResult<V>()
    data class Error(val error: PackageManagerError, val message: String) : PackageManagerResult<Nothing>()

    fun getValueOrNull(): V? = getOrElse { null }

    val isError
        get() = this is Error

    val isSuccess
        get() = this is Success
}

internal inline fun <R, T : R> PackageManagerResult<T>.getOrElse(
    onError: (PackageManagerResult.Error) -> R?
): R? {
    return when (this) {
        is PackageManagerResult.Success -> this.value
        is PackageManagerResult.Error -> onError(this)
    }
}