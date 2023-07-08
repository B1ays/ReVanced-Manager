package ru.Blays.ReVanced.Manager.UI.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.Blays.ReVanced.Manager.Repository.DownloadsRepository
import ru.Blays.ReVanced.Manager.Repository.SettingsRepository
import ru.blays.revanced.Services.PublicApi.PackageManagerApi
import ru.blays.revanced.data.Downloader.DownloadTask
import ru.blays.revanced.data.Downloader.build
import ru.blays.revanced.domain.DataClasses.AppUpdateModelDto
import ru.blays.revanced.domain.UseCases.GetChangelogUseCase
import ru.blays.revanced.domain.UseCases.GetUpdateInfoUseCase

private const val RELEASE_CHANNEL_JSON_LINK = "https://raw.githubusercontent.com/B1ays/ReVanced-Manager/master/UpdateRelease.json"

class AppUpdateScreenViewModel(
    private val getUpdateInfoUseCase: GetUpdateInfoUseCase,
    private val getChangelogUseCase: GetChangelogUseCase,
    private val packageManagerApi: PackageManagerApi,
    private val settingsRepository: SettingsRepository,
    private val downloadsRepository: DownloadsRepository
): BaseViewModel() {

    var updateInfo: MutableStateFlow<AppUpdateModelDto?> = MutableStateFlow(null)

    var changelog by mutableStateOf("")

    init {
        launch {
            val model = getUpdateInfoUseCase.execute(RELEASE_CHANNEL_JSON_LINK)
            updateInfo.value = model
            changelog = model?.let { getChangelogUseCase.execut(it.changelogLink) } ?: ""
        }
    }

    @Suppress("DeferredResultUnused")
    fun downloadAndInstall() {
        val model = updateInfo.value
        model?.let {
            val task = DownloadTask(
                url = it.apkLink,
                fileName = "Update_${it.availableVersion} (${it.versionCode})"
            ).setDefaultActions(
                onSuccess = {
                    packageManagerApi.installApk(file, settingsRepository.installerType)
                }
            ).build()
            downloadsRepository.addToList(task)
        }
    }
}