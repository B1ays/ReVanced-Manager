package ru.blays.revanced.Services.PublicApi

import ru.blays.revanced.Services.NonRootService.Interfaces.PackageManagerInterface
import ru.blays.revanced.Services.NonRootService.Interfaces.PackageManagerResult
import kotlinx.coroutines.Deferred
import java.io.File

interface PackageManagerApi {

    val packageManagerInterface: PackageManagerInterface

    fun installApk(file: File, installerType: Int): Deferred<PackageManagerResult<Nothing>>

    fun installSplitApks(files: List<File>, installerType: Int)

    fun uninstall(packageName: String)

    fun launchApp(packageName: String)

    fun getVersionName(packageName: String): Deferred<PackageManagerResult<String>>

}