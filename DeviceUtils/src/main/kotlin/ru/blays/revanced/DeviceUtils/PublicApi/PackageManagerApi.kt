package ru.blays.revanced.DeviceUtils.PublicApi

import ru.blays.revanced.DeviceUtils.Interfaces.PackageManagerInterface
import ru.blays.revanced.DeviceUtils.Interfaces.PackageManagerResult
import java.io.File

interface PackageManagerApi {

    val packageManagerInterface: PackageManagerInterface

    suspend fun installApk(file: File, installerType: Int): PackageManagerResult<Nothing>

    suspend fun installSplitApks(files: List<File>, installerType: Int)

    suspend fun uninstall(packageName: String)

    suspend fun launchApp(packageName: String)

    suspend fun getVersionName(packageName: String): PackageManagerResult<String>

    suspend fun getVersionCode(packageName: String): PackageManagerResult<Int>

}