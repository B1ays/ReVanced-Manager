package ru.blays.revanced.Services.NonRootService.PackageManager

import com.topjohnwu.superuser.Shell
import ru.blays.revanced.Services.RootService.Util.SuException
import ru.blays.revanced.Services.RootService.Util.awaitOutputOrThrow
import ru.blays.revanced.Services.RootService.Util.errString
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class RootPackageManager: PackageManagerInterface {

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
            val versionName = dumpsys.split('=').last()

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