package ru.blays.revanced.NonRootService.Installer

import com.vanced.manager.repository.manager.PackageManagerResult

abstract class AppInstallerInterface {

    abstract suspend fun install(appVersions: List<String>?)

    abstract suspend fun installRoot(appVersions: List<String>?): PackageManagerResult<Nothing>

}