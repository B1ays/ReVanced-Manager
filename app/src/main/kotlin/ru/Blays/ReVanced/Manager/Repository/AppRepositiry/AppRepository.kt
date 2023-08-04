package ru.Blays.ReVanced.Manager.Repository.AppRepositiry

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.blays.revanced.Elements.DataClasses.AppInfo
import ru.blays.revanced.DeviceUtils.Root.ModuleIntstaller.ModuleInstaller
import ru.blays.revanced.domain.DataClasses.VersionsInfoModelDto
import ru.blays.revanced.domain.UseCases.GetVersionsListUseCase

class AppRepository private constructor(): AppRepositoryInterface {

    override val coroutineContext = Dispatchers.IO

    override var getVersionsListUseCase: GetVersionsListUseCase? = null

    override var appName = ""

    override var catalogUrl = ""

    override var moduleType: ModuleInstaller.Module? = null

    override var remoteVersionsList: MutableList<VersionsInfoModelDto> = mutableListOf()

    override val appVersions: MutableList<AppVersionModel> = mutableListOf()

    override val hasRootVersion: Boolean
        get() = appVersions.any { it.isRootNeeded }

    override var availableVersion: MutableStateFlow<String?> = MutableStateFlow(null)

    override fun version(scope: AppVersionModel.() -> Unit) {
        val appVersionModel = AppVersionModel()
        scope(appVersionModel)
        requireNotNull(appVersionModel.versionName) { "No value passed for [versionName]" }
        requireNotNull(appVersionModel.localVersionSource) { "No value passed for [localVersionSource]" }
        requireNotNull(appVersionModel.remoteVersionSource) { "No value passed for [remoteVersionsListSource]" }
        appVersions.add(appVersionModel)
    }

    override fun createAppInfo(versionModel: AppVersionModel): AppInfo = AppInfo(
        appName = appName,
        version = versionModel.localVersionName,
        patchesVersion = versionModel.patchesVersion,
        packageName = versionModel.packageName
    )

    override fun updateVersionsList() {
        launch {
            getVersionsListUseCase?.let { useCase ->
                if (remoteVersionsList.isEmpty()) {
                    remoteVersionsList.addAll(useCase.execut(catalogUrl, true))
                } else {
                    remoteVersionsList = useCase.execut(catalogUrl, true).toMutableList()
                }
            }
            availableVersion.value = remoteVersionsList.firstOrNull()?.version
        }
    }

    init {
        updateVersionsList()
    }

    companion object {
        fun builder(scope: AppRepositoryInterface.() -> Unit): AppRepository {
            val repository = AppRepository()
            scope(repository)
            require(repository.appName.isNotEmpty()) { "No value passed for [appName]" }
            require(repository.catalogUrl.isNotEmpty()) { "No value passed for [catalogUrl]" }
            require(
                if (repository.appVersions.any { it.isRootNeeded }) {
                    repository.moduleType != null
                } else true
            ) { "No value passed for [moduleType]. App has Root version" }
            requireNotNull(repository.getVersionsListUseCase) { "No value passed for [catalogUrl]" }
            require(repository.appVersions.isNotEmpty()) { "At least one version must be added" }
            return repository
        }
    }
}