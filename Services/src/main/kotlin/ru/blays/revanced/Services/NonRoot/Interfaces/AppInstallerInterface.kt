package ru.blays.revanced.Services.NonRoot.Interfaces

internal abstract class AppInstallerInterface {

    abstract suspend fun install(appVersions: List<String>?)

    abstract suspend fun installRoot(appVersions: List<String>?): PackageManagerResult<Nothing>

}