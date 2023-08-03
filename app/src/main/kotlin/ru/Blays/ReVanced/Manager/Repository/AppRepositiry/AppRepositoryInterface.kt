package ru.Blays.ReVanced.Manager.Repository.AppRepositiry

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import ru.blays.revanced.Elements.DataClasses.AppInfo
import ru.blays.revanced.DeviceUtils.Root.ModuleIntstaller.ModuleInstaller
import ru.blays.revanced.domain.DataClasses.VersionsInfoModelDto
import ru.blays.revanced.domain.UseCases.GetVersionsListUseCase

interface AppRepositoryInterface: CoroutineScope {

    var getVersionsListUseCase: GetVersionsListUseCase?

    var appName: String

    var catalogUrl: String

    var moduleType: ModuleInstaller.Module?

    var remoteVersionsList: MutableList<VersionsInfoModelDto>

    val appVersions: MutableList<AppVersionModel>

    val hasRootVersion: Boolean

    var availableVersion: MutableStateFlow<String?>

    fun version(scope: AppVersionModel.() -> Unit)

    fun createAppInfo(versionModel: AppVersionModel): AppInfo

    fun updateVersionsList()
}