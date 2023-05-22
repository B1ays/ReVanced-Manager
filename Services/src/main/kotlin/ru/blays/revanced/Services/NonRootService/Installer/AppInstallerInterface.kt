package ru.blays.revanced.Services.NonRootService.Installer

import ru.blays.revanced.Services.NonRootService.PackageManager.PackageManagerResult

internal abstract class AppInstallerInterface {

    abstract suspend fun install(appVersions: List<String>?)

    abstract suspend fun installRoot(appVersions: List<String>?): PackageManagerResult<Nothing>

}