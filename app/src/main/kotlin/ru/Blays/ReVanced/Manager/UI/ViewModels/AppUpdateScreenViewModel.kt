package ru.Blays.ReVanced.Manager.UI.ViewModels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.Blays.ReVanced.Manager.BuildConfig
import ru.Blays.ReVanced.Manager.Repository.DownloadsRepository
import ru.Blays.ReVanced.Manager.Repository.SettingsRepository
import ru.blays.revanced.Services.PublicApi.PackageManagerApi
import ru.blays.revanced.data.Downloader.DownloadTask
import ru.blays.revanced.data.Downloader.build
import ru.blays.revanced.domain.DataClasses.AppUpdateModelDto
import ru.blays.revanced.domain.UseCases.GetChangelogUseCase
import ru.blays.revanced.domain.UseCases.GetUpdateInfoUseCase
import ru.blays.revanced.shared.LogManager.BLog

private const val TAG = "appUpdateViewModel"

private const val RELEASE_CHANNEL_JSON_LINK = "https://raw.githubusercontent.com/B1ays/ReVanced-Manager/master/UpdateRelease.json"

class AppUpdateScreenViewModel(
    private val getUpdateInfoUseCase: GetUpdateInfoUseCase,
    private val getChangelogUseCase: GetChangelogUseCase,
    private val packageManagerApi: PackageManagerApi,
    private val settingsRepository: SettingsRepository,
    private val downloadsRepository: DownloadsRepository
): BaseViewModel() {

    private var _updateInfo: MutableStateFlow<AppUpdateModelDto?> = MutableStateFlow(null)

    private var _changelog: MutableStateFlow<String> = MutableStateFlow("")

    val updateInfo: AppUpdateModelDto?
        @Composable get() = _updateInfo.collectAsState().value

    val changelog: String
        @Composable get() = _changelog.collectAsState().value

    var isUpdateAvailable by mutableStateOf(false)

    private suspend fun compareVersionCodes() = coroutineScope {
        val installedVersionCode = packageManagerApi.getVersionCode(BuildConfig.APPLICATION_ID).await().getValueOrNull()
        _updateInfo.value?.let { dataModel ->
            isUpdateAvailable = (installedVersionCode ?: Int.MAX_VALUE) < dataModel.versionCode
        }
    }

    @Suppress("DeferredResultUnused")
    fun downloadAndInstall() {
        val model = _updateInfo.value
        model?.let { infoModel ->
            val task = DownloadTask(
                url = infoModel.apkLink,
                fileName = "Update_${infoModel.availableVersion} (${infoModel.versionCode})"
            ).setDefaultActions(
                onSuccess = {
                    packageManagerApi.installApk(file!!, settingsRepository.installerType)
                }
            ).build()
            task?.let { task -> downloadsRepository.addToList(task) }
        }
    }

    init {
        BLog.i(TAG, "Init App update view model")
        launch {
            val model = getUpdateInfoUseCase.execute(RELEASE_CHANNEL_JSON_LINK, recreateCache = true)
            _updateInfo.emit(model)
            val changelog = model?.let { getChangelogUseCase.execut(it.changelogLink, recreateCache = true) } ?: ""
            _changelog.emit(changelog)
            compareVersionCodes()
        }
    }
}