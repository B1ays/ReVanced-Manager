package ru.blays.revanced.Services.PublicApi

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ru.blays.revanced.Services.NonRootService.PackageManager.NonRootPackageManager
import ru.blays.revanced.Services.NonRootService.PackageManager.PackageManagerInterface
import ru.blays.revanced.Services.NonRootService.PackageManager.RootPackageManager
import ru.blays.revanced.Services.RootService.Util.isRootGranted
import java.io.File

class PackageManagerApiImpl(context: Context, installerType: Int): PackageManagerApi, CoroutineScope {

    override val coroutineContext = Dispatchers.Default

    override val packageManagerInterface: PackageManagerInterface = when {
        installerType == 1 -> NonRootPackageManager(context)
        installerType == 2 && isRootGranted -> RootPackageManager()
        else -> NonRootPackageManager(context)
    }

    override fun installApk(file: File, installerType: Int) = async {
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
        launch {
            packageManagerInterface.uninstallApp(packageName)
        }
    }

    override fun launchApp(packageName: String) {
        launch {
            packageManagerInterface.launchApp(packageName)
        }
    }

    override fun getVersionName(packageName: String) = async {
        packageManagerInterface.getVersionName(packageName)
    }

}
