package ru.blays.revanced.Services.PublicApi

import com.vanced.manager.repository.manager.PackageManager
import com.vanced.manager.repository.manager.PackageManagerResult
import kotlinx.coroutines.Deferred
import java.io.File

interface PackageManagerApi {

    val packageManager: PackageManager

    fun installApk(file: File, installerType: Int)

    fun installSplitApks(files: List<File>, installerType: Int)

    fun uninstall(packageName: String)

    fun launchApp(packageName: String)

    fun getVersionName(packageName: String): Deferred<PackageManagerResult<String>>

}