package ru.blays.revanced.RootService.Util

import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.nio.ExtendedFile
import com.vanced.manager.repository.manager.PackageManagerError
import com.vanced.manager.repository.manager.PackageManagerResult
import com.vanced.manager.repository.manager.getEnumForInstallFailed
import com.vanced.manager.util.errString
import com.vanced.manager.util.outString
import java.io.File
import java.io.IOException

object PMRoot {

    fun installApp(apkPath: String): PackageManagerResult<Nothing> {
        val apk = File(apkPath)
        val tmpApk = copyApkToTemp(apk).getOrElse { exception ->
            return PackageManagerResult.Error(
                PackageManagerError.SESSION_FAILED_COPY,
                exception.stackTraceToString()
            )
        }

        val install = Shell.cmd("pm", "install", "-r", tmpApk.absolutePath).exec()

        tmpApk.delete()

        if (!install.isSuccess) {
            val errString = install.errString
            return PackageManagerResult.Error(getEnumForInstallFailed(errString), errString)
        }

        return PackageManagerResult.Success(null)
    }

    fun installSplitApp(apkPaths: List<String>): PackageManagerResult<Nothing> {
        val installCreate = Shell.cmd("pm", "install-create", "-r").exec()

        if (!installCreate.isSuccess)
            return PackageManagerResult.Error(
                PackageManagerError.SESSION_FAILED_CREATE,
                installCreate.errString
            )

        val sessionId = installCreate.outString

        if (sessionId.toIntOrNull() == null)
            return PackageManagerResult.Error(
                PackageManagerError.SESSION_INVALID_ID,
                installCreate.errString
            )

        for (apkPath in apkPaths) {
            val apk = File(apkPath)
            val tmpApk = copyApkToTemp(apk).getOrElse { exception ->
                return PackageManagerResult.Error(
                    PackageManagerError.SESSION_FAILED_COPY,
                    exception.stackTraceToString()
                )
            }

            val installWrite =
                Shell.cmd("pm", "install-write", sessionId, tmpApk.name, tmpApk.absolutePath)
                    .exec()

            tmpApk.delete()

            if (!installWrite.isSuccess)
                return PackageManagerResult.Error(
                    PackageManagerError.SESSION_FAILED_WRITE,
                    installWrite.errString
                )
        }

        val installCommit = Shell.cmd("pm", "install-commit", sessionId).exec()

        if (!installCommit.isSuccess) {
            val errString = installCommit.errString
            return PackageManagerResult.Error(getEnumForInstallFailed(errString), errString)
        }

        return PackageManagerResult.Success(null)
    }

    fun uninstallApp(pkg: String): PackageManagerResult<Nothing> {
        val uninstall = Shell.cmd("pm", "uninstall", pkg).exec()

        if (!uninstall.isSuccess)
            return PackageManagerResult.Error(
                PackageManagerError.UNINSTALL_FAILED,
                uninstall.errString
            )

        return PackageManagerResult.Success(null)
    }

    fun setInstallerPackage(targetPkg: String, installerPkg: String): PackageManagerResult<Nothing> {
        val setInstaller = Shell.cmd("pm", "set-installer", targetPkg, installerPkg)
            .exec()

        if (!setInstaller.isSuccess)
            return PackageManagerResult.Error(
                PackageManagerError.SET_FAILED_INSTALLER,
                setInstaller.errString
            )

        return PackageManagerResult.Success(null)
    }

    fun forceStopApp(pkg: String): PackageManagerResult<Nothing> {
        val stopApp = Shell.cmd("am", "force-stop", pkg).exec()

        if (!stopApp.isSuccess)
            return PackageManagerResult.Error(
                PackageManagerError.APP_FAILED_FORCE_STOP,
                stopApp.errString
            )

        return PackageManagerResult.Success(null)
    }

    fun getPackageVersionName(pkg: String): PackageManagerResult<String> {
        val keyword = "versionName="
        val dumpsys = Shell.cmd("dumpsys", "package", pkg, "|", "grep", keyword).exec()

        if (!dumpsys.isSuccess)
            return PackageManagerResult.Error(
                PackageManagerError.GET_FAILED_PACKAGE_VERSION_NAME,
                dumpsys.errString
            )

        return PackageManagerResult.Success(dumpsys.outString.removePrefix(keyword))
    }

    fun getPackageVersionCode(pkg: String): PackageManagerResult<Long> {
        val keyword = "versionCode="
        val dumpsys = Shell.cmd("dumpsys", "package", pkg, "|", "grep", keyword).exec()

        if (!dumpsys.isSuccess)
            return PackageManagerResult.Error(
                PackageManagerError.GET_FAILED_PACKAGE_VERSION_CODE,
                dumpsys.errString
            )

        return PackageManagerResult.Success(
            dumpsys.outString.removePrefix(keyword).substringAfter("minSdk")
                .toLong()
        )
    }

    fun getPackageDir(pkg: String): PackageManagerResult<String> {
        val keyword = "path: "
        val dumpsys = Shell.cmd("dumpsys", "package", pkg, "|", "grep", keyword).exec()

        if (!dumpsys.isSuccess)
            return PackageManagerResult.Error(
                PackageManagerError.GET_FAILED_PACKAGE_DIR,
                dumpsys.errString
            )

        return PackageManagerResult.Success(dumpsys.outString.removePrefix(keyword))
    }

    private fun copyApkToTemp(apk: File): Result<ExtendedFile> {

        val fileManager = FileSystemManagerImplementation()

        val tmpPath = "/data/local/tmp/${apk.name}"

        val tmpApk = fileManager.getFile(tmpPath).apply {
            createNewFile()
        }

        try {
            tmpApk.outputStream().use {
                it.write(apk.readBytes())
                it.flush()
            }
        } catch (e: IOException) {
            return Result.failure(e)
        }

        return Result.success(tmpApk)
    }

}