package ru.blays.revanced.Services.PublicApi

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ru.blays.revanced.Services.NonRootService.Interfaces.PackageManagerInterface
import ru.blays.revanced.Services.NonRootService.PackageManager.NonRootPackageManager
import ru.blays.revanced.Services.RootService.PackageManager.RootPackageManager
import ru.blays.revanced.Services.RootService.Util.isRootGranted
import ru.blays.revanced.shared.LogManager.BLog
import java.io.File

private const val TAG = "PackageManager Api"

class PackageManagerApiImpl(context: Context, installerType: Int): PackageManagerApi, CoroutineScope {

    override val coroutineContext = Dispatchers.Default

    override val packageManagerInterface: PackageManagerInterface = when {
        installerType == 1 -> NonRootPackageManager(context)
        installerType == 2 && isRootGranted -> RootPackageManager()
        else -> NonRootPackageManager(context)
    }

    override fun installApk(file: File, installerType: Int) = async {
        BLog.i(TAG, "install apk: ${file.nameWithoutExtension}")
        packageManagerInterface.installApp(file)
    }

    override fun installSplitApks(
        files: List<File>,
        installerType: Int
    ) {
        launch {
            packageManagerInterface.installSplitApp(apks = files.toTypedArray())
        }
    }

    override fun uninstall(packageName: String) {
        BLog.i(TAG, "Uninstall app: $packageName")
        launch {
            packageManagerInterface.uninstallApp(packageName)
        }
    }

    override fun launchApp(packageName: String) {
        BLog.i(TAG, "Launch app: $packageName")
        launch {
            packageManagerInterface.launchApp(packageName)
        }
    }

    override fun getVersionName(packageName: String) = async {
        BLog.i(TAG, "Get version name for: $packageName")
        packageManagerInterface.getVersionName(packageName)
    }

}
