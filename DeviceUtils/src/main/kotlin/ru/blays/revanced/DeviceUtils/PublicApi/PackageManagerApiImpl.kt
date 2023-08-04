package ru.blays.revanced.DeviceUtils.PublicApi

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import ru.blays.revanced.DeviceUtils.Interfaces.PackageManagerInterface
import ru.blays.revanced.DeviceUtils.Interfaces.PackageManagerResult
import ru.blays.revanced.DeviceUtils.NonRoot.PackageManager.NonRootPackageManager
import ru.blays.revanced.DeviceUtils.Root.PackageManager.RootPackageManager
import ru.blays.revanced.DeviceUtils.Root.Util.isRootGranted
import ru.blays.revanced.shared.LogManager.BLog
import java.io.File

private const val TAG = "PackageManager Api"

class PackageManagerApiImpl(context: Context, installerType: Int): PackageManagerApi, CoroutineScope {

    override val coroutineContext = Dispatchers.Default

    override val packageManagerInterface: PackageManagerInterface = when {
        installerType == 0 -> NonRootPackageManager(context)
        installerType == 1 && isRootGranted -> RootPackageManager()
        else -> NonRootPackageManager(context)
    }

    override suspend fun installApk(file: File, installerType: Int): PackageManagerResult<Nothing> {
        BLog.i(TAG, "install apk: ${file.nameWithoutExtension}")
        return packageManagerInterface.installApp(file)
    }

    override suspend fun installSplitApks(
        files: List<File>,
        installerType: Int
    ) {
        packageManagerInterface.installSplitApp(apks = files.toTypedArray())
    }

    override suspend fun uninstall(packageName: String) {
        BLog.i(TAG, "Uninstall app: $packageName")
        packageManagerInterface.uninstallApp(packageName)
    }

    override suspend fun launchApp(packageName: String) {
        BLog.i(TAG, "Launch app: $packageName")
        packageManagerInterface.launchApp(packageName)
    }

    override suspend fun getVersionName(packageName: String): PackageManagerResult<String> {
        BLog.i(TAG, "Get version name for: $packageName")
        return packageManagerInterface.getVersionName(packageName)
    }

    override suspend fun getVersionCode(packageName: String): PackageManagerResult<Int> {
        return packageManagerInterface.getVersionCode(packageName)
    }

}
