package ru.Blays.ReVanced.Manager.Repository.AppRepositiry

import kotlinx.coroutines.CoroutineScope
import ru.blays.revanced.Elements.DataClasses.AppInfo
import ru.blays.revanced.Services.RootService.Util.MagiskInstaller
import ru.blays.revanced.domain.DataClasses.VersionsInfoModelDto
import ru.blays.revanced.domain.UseCases.GetVersionsListUseCase

interface AppRepositoryInterface: CoroutineScope {

    var getVersionsListUseCase: GetVersionsListUseCase?

    var appName: String

    var appType: String

    var moduleType: MagiskInstaller.Module?

    var remoteVersionsList: MutableList<VersionsInfoModelDto>

    val appVersions: MutableList<AppVersionModel>

    val hasRootVersion: Boolean

    fun version(scope: AppVersionModel.() -> Unit)

    fun createAppInfo(versionModel: AppVersionModel): AppInfo
}