package ru.Blays.ReVanced.Manager.UI.ViewModels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.get
import org.koin.java.KoinJavaComponent.inject
import ru.Blays.ReVanced.Manager.Data.Apps
import ru.Blays.ReVanced.Manager.Data.MagiskInstallerState
import ru.Blays.ReVanced.Manager.Repository.SettingsRepository
import ru.Blays.ReVanced.Manager.Repository.VersionsRepository
import ru.blays.revanced.Elements.DataClasses.MagiskInstallerAlertDialogState
import ru.blays.revanced.Elements.DataClasses.RootVersionDownloadModel
import ru.blays.revanced.Elements.Elements.Screens.VersionsInfoScreen.DownloadProgressContent
import ru.blays.revanced.Elements.GlobalState.NavBarExpandedContent
import ru.blays.revanced.Services.PublicApi.PackageManagerApi
import ru.blays.revanced.Services.RootService.PackageManager.RootPackageManager
import ru.blays.revanced.Services.RootService.Util.MagiskInstaller
import ru.blays.revanced.Services.RootService.Util.isRootGranted
import ru.blays.revanced.data.Downloader.DataClass.DownloadInfo
import ru.blays.revanced.data.Downloader.DownloadTask
import ru.blays.revanced.data.Downloader.build
import ru.blays.revanced.domain.DataClasses.ApkInfoModelDto
import ru.blays.revanced.domain.DataClasses.VersionsInfoModelDto
import ru.blays.revanced.domain.UseCases.GetApkListUseCase
import ru.blays.revanced.domain.UseCases.GetChangelogUseCase
import ru.blays.revanced.domain.UseCases.GetVersionsListUseCase
import ru.blays.revanced.shared.Extensions.collect

class VersionsListScreenViewModel(
    private val getVersionsListUseCase: GetVersionsListUseCase,
    private val getApkListUseCase: GetApkListUseCase,
    private val getChangelogUseCase: GetChangelogUseCase
) : ViewModel(), CoroutineScope {

    // Coroutine scope for launch suspend functions
    override val coroutineContext = Dispatchers.IO

    // UI states
    var isRefreshing by mutableStateOf(false)

    var list by mutableStateOf(emptyList<VersionsInfoModelDto>())

    var isApkListBottomSheetExpanded = MutableStateFlow(false)

    var isChangelogBottomSheetExpanded = MutableStateFlow(false)

    var magiskInstallerDialogState by mutableStateOf(MagiskInstallerAlertDialogState())

    var bottomSheetList = MutableStateFlow(emptyList<ApkInfoModelDto>())

    var changelog = MutableStateFlow("")

    var pagesCount by mutableIntStateOf(0)

    private val packageManager: PackageManagerApi = get(PackageManagerApi::class.java)

    private val settingsRepository: SettingsRepository = get(SettingsRepository::class.java)

    private var app: Apps? = null

    var repository: VersionsRepository? = null

    fun getAppsEnumByAppType(appType: String) {
        app = when(appType) {
            Apps.YOUTUBE.repository.appType -> Apps.YOUTUBE
            Apps.YOUTUBE_MUSIC.repository.appType -> Apps.YOUTUBE_MUSIC
            Apps.MICROG.repository.appType -> Apps.MICROG
            else -> null
        }
        app?.let { app -> getDataFromRepository(app.repository)}
    }

    private fun getDataFromRepository(repo: VersionsRepository) {
        repository = repo
        calculatePagesCount(repo)
        if (repo.versionsList.isNotEmpty()) {
            list = repo.versionsList
        } else {
            launch { getList(repo.appType) }
        }
    }

    private fun calculatePagesCount(repo: VersionsRepository) {
        pagesCount = if (repo.hasRootVersion && isRootGranted) 2 else 1
    }

    private suspend fun getList(appType: String) = withContext(Dispatchers.IO) {
        isRefreshing = true
        list = getVersionsListUseCase.execut(appType)
        isRefreshing = false
    }

    fun onRefresh() {
        launch { repository?.updateInfo(recreateCache = true) }
    }

    val hideRebootAlertDialog = { magiskInstallerDialogState = MagiskInstallerAlertDialogState() }
    val showRebootAlertDialog = { magiskInstallerDialogState = magiskInstallerDialogState.copy(isExpanded = true) }

    suspend fun showApkListBottomSheet(url: String, rootVersion: Boolean) {
        bottomSheetList.value = getApkListUseCase.execute(url)?.filter {
            it.isRootVersion == rootVersion
        } ?: emptyList()
        isApkListBottomSheetExpanded.emit(true)
    }

    suspend fun showChangelogBottomSheet(url: String) {
        changelog.emit(getChangelogUseCase.execut(url))
        isChangelogBottomSheetExpanded.emit(true)
    }

    fun delete(packageName: String) {
        packageManager.uninstall(packageName)
        onRefresh()
    }

    fun deleteModule(packageName: String) {
        repository?.moduleType?.let { module ->
            MagiskInstaller.delete(module = module)
        }
        onRefresh()
    }

    fun launch(packageName: String) {
        packageManager.launchApp(packageName)
    }

    fun reboot() {
        Shell.cmd("am start -a android.intent.action.REBOOT").exec()
    }

    @Suppress("DeferredResultUnused")
    fun downloadNonRootVersion(
        fileName: String,
        url: String
    ) {

        val task = DownloadTask(url, fileName)
            .setDefaultActions(
                onSuccess = {
                    packageManager.installApk(file, settingsRepository.installerType)
                    NavBarExpandedContent.hide()
                    onRefresh()
                },
                onError = {
                    NavBarExpandedContent.hide()
                },
                onCancel = {
                    file.delete()
                    NavBarExpandedContent.hide()
                }
            )
            .build()

        NavBarExpandedContent.setContent { DownloadProgressContent(downloadInfo = task) }
    }

    fun downloadRootVersion(
        filesModel: RootVersionDownloadModel
    ) {

        if (filesModel.origUrl == null) return

        val context: Context by inject(Context::class.java)

        val stateList = mutableStateListOf<DownloadInfo>()

        NavBarExpandedContent.setContent { DownloadProgressContent(downloadStateList = stateList) }

        val state = MutableStateFlow(MagiskInstallerState())

        val origApkDownloadTask = DownloadTask(url = filesModel.origUrl!!, fileName = filesModel.fileName + "-orig")
            .setDefaultActions(
                onSuccess = {
                    Log.d("DownloadCallback", "orig apk download success")

                    launch {
                        with(state) { emit(value.copy(origApkDownloaded = true)) }

                        val installResult = async {
                            RootPackageManager().installApp(file)
                        }.await()

                        if (installResult.isError) {
                            this.cancel()
                            return@launch
                        }
                        with(state) { emit(value.copy(origApkInstalled = true)) }
                    }
                },
                onError = {
                    file.delete()
                    NavBarExpandedContent.hide()
                },
                onCancel = {
                    file.delete()
                    NavBarExpandedContent.hide()
                }
            )
            .build()
            .also { downloadInfo ->
                stateList.add(downloadInfo)
                collect(state) { state ->
                    if (state.origApkDownloaded && state.modApkDownloaded) {
                        NavBarExpandedContent.hide()
                    } else if (state.origApkDownloaded) {
                        stateList.remove(downloadInfo)
                    }
                }
            }

        val modApkDownloadTask = DownloadTask(url = filesModel.modUrl, fileName = filesModel.fileName)
            .setDefaultActions(
                onSuccess = {

                    Log.d("DownloadCallback", "mod apk download success")

                    launch { with(state) { emit(value.copy(modApkDownloaded = true)) } }

                    collect(state) {

                        if (it.origApkInstalled) {
                            launch {
                                Log.d("ViewModel", "start installer")
                                repository?.moduleType?.let { module ->
                                    MagiskInstaller.install(
                                        module,
                                        file,
                                        context
                                    )
                                    file.delete()
                                    origApkDownloadTask.file.delete()
                                    onRefresh()
                                }
                            }
                            magiskInstallerDialogState = MagiskInstallerAlertDialogState(MagiskInstaller.status, true)
                        }
                    }
                },
                onError = {
                    file.delete()
                    origApkDownloadTask.file.delete()
                    NavBarExpandedContent.hide()
                },
                onCancel = {
                    file.delete()
                    NavBarExpandedContent.hide()
                }
            )
            .build()
            .also { downloadInfo ->
                stateList.add(downloadInfo)
                collect(state) { state ->
                    if (state.origApkDownloaded && state.modApkDownloaded) {
                        NavBarExpandedContent.hide()
                    } else if (state.modApkDownloaded) {
                        stateList.remove(downloadInfo)
                    }
                }
            }
    }
}