package ru.blays.revanced.Services.PublicApi

import android.content.Context
import com.vanced.manager.repository.manager.NonrootPackageManager
import com.vanced.manager.repository.manager.PackageManager
import com.vanced.manager.repository.manager.RootPackageManager
import com.vanced.manager.util.isRootGranted
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.CoroutineContext

class PackageManagerApiImpl(context: Context, private val installerType: Int): PackageManagerApi, CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Default

    override val packageManager: PackageManager = when {
        installerType == 1 -> NonrootPackageManager(context)
        installerType == 2 && isRootGranted -> RootPackageManager()
        else -> NonrootPackageManager(context)
    }

    override fun installApk(file: File, installerType: Int) {
        launch {
            packageManager.installApp(file)
        }
    }

    override fun installSplitApks(
        files: List<File>,
        installerType: Int
    ) {
        launch {
            packageManager.installSplitApp(apks = files.toTypedArray())
        }
    }

    override fun uninstall(packageName: String) {
        launch {
            packageManager.uninstallApp(packageName)
        }
    }

    override fun launchApp(packageName: String) {
        launch {
            packageManager.launchApp(packageName)
        }
    }

    override fun getVersionName(packageName: String) = async {
        packageManager.getVersionName(packageName)
    }

}
