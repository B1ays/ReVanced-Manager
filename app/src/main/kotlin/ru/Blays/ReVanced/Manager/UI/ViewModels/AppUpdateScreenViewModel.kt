package ru.Blays.ReVanced.Manager.UI.ViewModels

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.Blays.ReVanced.Manager.BuildConfig
import ru.Blays.ReVanced.Manager.DI.autoInject
import ru.Blays.ReVanced.Manager.Repository.DownloadsRepository
import ru.Blays.ReVanced.Manager.UI.ComponentCallback.IComponentCallback
import ru.Blays.ReVanced.Manager.Utils.DownloaderLogAdapter.LogAdapterBLog
import ru.blays.downloader.DataClass.StorageMode
import ru.blays.downloader.DownloadTask
import ru.blays.downloader.build
import ru.blays.preference.DataStores.DownloadsFolderUriDS
import ru.blays.preference.DataStores.InstallerTypeDS
import ru.blays.preference.DataStores.StorageAccessTypeDS
import ru.blays.revanced.DeviceUtils.PublicApi.PackageManagerApi
import ru.blays.revanced.domain.DataClasses.AppUpdateModelDto
import ru.blays.revanced.domain.UseCases.GetChangelogUseCase
import ru.blays.revanced.domain.UseCases.GetUpdateInfoUseCase
import ru.blays.revanced.shared.Data.APK_FILE_EXTENSION
import ru.blays.revanced.shared.Data.APK_MIME_TYPE
import ru.blays.revanced.shared.Data.DEFAULT_INSTALLER_CACHE_FOLDER
import ru.blays.revanced.shared.LogManager.BLog
import ru.blays.simpledocument.SimpleDocument
import java.io.File

private const val TAG = "appUpdateViewModel"

private const val RELEASE_CHANNEL_JSON_LINK = "https://raw.githubusercontent.com/B1ays/ReVanced-Manager/master/UpdateRelease.json"

class AppUpdateScreenViewModel(
    private val getUpdateInfoUseCase: GetUpdateInfoUseCase,
    private val getChangelogUseCase: GetChangelogUseCase,
    private val packageManagerApi: PackageManagerApi,
    private val downloadsRepository: DownloadsRepository,
    private val context: Context
): BaseViewModel() {

    private var _updateInfo: MutableStateFlow<AppUpdateModelDto?> = MutableStateFlow(null)

    private var _changelog: MutableStateFlow<String> = MutableStateFlow("")

    val updateInfo: AppUpdateModelDto?
        @Composable get() = _updateInfo.collectAsState().value

    val changelog: String
        @Composable get() = _changelog.collectAsState().value

    var isUpdateAvailable by mutableStateOf(false)

    private suspend fun compareVersionCodes() = coroutineScope {
        val installedVersionCode = packageManagerApi.getVersionCode(BuildConfig.APPLICATION_ID).getValueOrNull()
        _updateInfo.value?.let { dataModel ->
            isUpdateAvailable = (installedVersionCode ?: Int.MAX_VALUE) < dataModel.versionCode
        }
    }

    @Suppress("LocalVariableName")
    fun downloadAndInstall(callback: IComponentCallback<() -> Unit>) {
        val _installerType: InstallerTypeDS by autoInject()
        val _storageMode: StorageAccessTypeDS by autoInject()
        val _downloadsFolderURI: DownloadsFolderUriDS by autoInject()
        val installerType by _installerType
        val storageMode by _storageMode
        val downloadsFolderUri by _downloadsFolderURI
        val model = _updateInfo.value
        model?.let { infoModel ->
            DownloadTask.Companion.builder {
                url = infoModel.apkLink
                fileName = "Update_${infoModel.availableVersion} (${infoModel.versionCode})"
                logAdapter = LogAdapterBLog()
                when(storageMode) {
                    0 -> {
                        this.storageMode = StorageMode.FileIO
                        onSuccess {
                            launch {
                                packageManagerApi.installApk(file!!, installerType)
                            }
                        }
                    }
                    1 -> {
                        simpleDocument = SimpleDocument.fromTreeUri(
                            downloadsFolderUri.toUri(),
                            context
                        )?.getOrCreateDocument(
                            fileName,
                            APK_FILE_EXTENSION,
                            APK_MIME_TYPE
                        )
                        this.storageMode = StorageMode.SAF
                        onSuccess {
                            launch {
                                val tmpFile = File(
                                    DEFAULT_INSTALLER_CACHE_FOLDER(context),
                                    fileName + APK_FILE_EXTENSION
                                ).apply {
                                    if (!exists()) createNewFile()
                                }
                                val copyToTemp = simpleDocument!!.copyTo(tmpFile)
                                if (!copyToTemp) return@launch
                                packageManagerApi.installApk(tmpFile, installerType)
                            }
                        }
                    }
                }
            }
            .build()
            ?.let {
                downloadsRepository.addToList(it)
            }
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