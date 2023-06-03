package ru.Blays.ReVanced.Manager.UI.ViewModels

import android.content.Context
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.os.Environment.getExternalStoragePublicDirectory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.Blays.ReVanced.Manager.DI.autoInject
import ru.Blays.ReVanced.Manager.Data.Apps
import ru.Blays.ReVanced.Manager.Repository.SettingsRepository
import ru.Blays.ReVanced.Manager.Repository.VersionsRepository
import ru.blays.revanced.Elements.DataClasses.RootVersionDownloadModel
import ru.blays.revanced.Elements.Elements.Screens.VersionsInfoScreen.DownloadProgressContent
import ru.blays.revanced.Elements.GlobalState.NavBarExpandedContent
import ru.blays.revanced.Services.PublicApi.PackageManagerApi
import ru.blays.revanced.Services.RootService.Util.MagiskInstaller
import ru.blays.revanced.Services.RootService.Util.isRootGranted
import ru.blays.revanced.data.Utils.DownloadState
import ru.blays.revanced.data.Utils.FileDownloadDto
import ru.blays.revanced.data.Utils.FileDownloader
import ru.blays.revanced.domain.DataClasses.ApkInfoModelDto
import ru.blays.revanced.domain.DataClasses.VersionsInfoModelDto
import ru.blays.revanced.domain.UseCases.GetApkListUseCase
import ru.blays.revanced.domain.UseCases.GetChangelogUseCase
import ru.blays.revanced.domain.UseCases.GetVersionsListUseCase
import java.io.File

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

    var isRebootAlertDialogShowed by mutableStateOf(false)

    var bottomSheetList = MutableStateFlow(emptyList<ApkInfoModelDto>())

    var changelog = MutableStateFlow("")

    var pagesCount by mutableIntStateOf(0)

    private val packageManager: PackageManagerApi by autoInject()

    private val settingsRepository: SettingsRepository by autoInject()

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

    suspend fun getList(appType: String) = withContext(Dispatchers.IO) {
        isRefreshing = true
        list = getVersionsListUseCase.execut(appType)
        isRefreshing = false
    }

    fun onRefresh() {
        launch { repository?.updateInfo() }
    }

    val hideRebootAlertDialog = { isRebootAlertDialogShowed = false }
    val showRebootAlertDialog = { isRebootAlertDialogShowed = true }

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

    fun downloadNonRootVersion(
        fileName: String,
        url: String
    ) {

        val file = File(getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS), "$fileName.apk")

        val fileDownloader = FileDownloader()

        val downloadState = fileDownloader.downloadFile(FileDownloadDto(url, file))

        val stateList = mutableStateListOf(downloadState)

        NavBarExpandedContent.setContent { DownloadProgressContent(downloadStateList = stateList) }

        waitDownloadAndInstall(
            state = downloadState,
            file = file
        )

    }

    fun downloadRootVersion(
        filesModel: RootVersionDownloadModel
    ) {

        if  (filesModel.origUrl == null) return

        val context: Context by autoInject()

        val fileDownloader = FileDownloader()

        val stateList = mutableStateListOf<DownloadState>()

        val modFile = File(getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS), filesModel.fileName)

        val origFile = File(getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS), "${filesModel.fileName}-orig.apk")

        NavBarExpandedContent.setContent { DownloadProgressContent(downloadStateList = stateList) }

        val modFileDownloadState = fileDownloader.downloadFile(
            FileDownloadDto(
                url = filesModel.modUrl,
                file = modFile
            )
        ).also {
            stateList.add(it)
        }

        val origFileDownloadState = fileDownloader.downloadFile(
            FileDownloadDto(
                url = filesModel.origUrl!!,
                file = origFile
            )
        ).also {
            stateList.add(it)
        }

        viewModelScope.launch {
            origFileDownloadState.downloadStatusFlow.collect { status ->
                if (status == FileDownloader.END_DOWNLOAD) {
                    val installResult = packageManager.installApk(origFile, installerType = settingsRepository.installerType).await()
                    if (installResult.isSuccess)
                        viewModelScope.launch {
                        modFileDownloadState.downloadStatusFlow.collect { status2 ->
                            if (status2 == FileDownloader.END_DOWNLOAD) {
                                NavBarExpandedContent.hide()
                                MagiskInstaller.install(repository?.moduleType!!, modFile, context)
                                showRebootAlertDialog()
                                onRefresh()
                            }
                        }
                    }
                }
            }
        }
    }


    @Suppress("DeferredResultUnused")
    private fun waitDownloadAndInstall(
        file: File,
        state: DownloadState
    ) {
        viewModelScope.launch {
            state.downloadStatusFlow.collect {
                if (it == FileDownloader.END_DOWNLOAD) {
                    packageManager.installApk(file, settingsRepository.installerType)
                    NavBarExpandedContent.hide()
                    onRefresh()
                }
            }
        }
    }

}