package ru.Blays.ReVanced.Manager.UI.ViewModels

import android.content.Context
import android.os.Environment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.get
import org.koin.java.KoinJavaComponent.inject
import ru.blays.revanced.Elements.DataClasses.AppInfo
import ru.blays.revanced.Elements.DataClasses.Apps
import ru.blays.revanced.Elements.DataClasses.NavBarExpandedContent
import ru.blays.revanced.Elements.Elements.Screens.VersionsInfoScreen.DownloadProgressContent
import ru.blays.revanced.Elements.Repository.SettingsRepository
import ru.blays.revanced.Elements.Repository.VersionsRepository
import ru.blays.revanced.Services.PublicApi.PackageManagerApi
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
) : ViewModel() {

    // Coroutine scope for launch suspend functions
    val coroutineScope = CoroutineScope(Dispatchers.IO)

    // UI states
    var isRefreshing by mutableStateOf(false)

    var list by mutableStateOf(emptyList<VersionsInfoModelDto>())

    var isApkListBottomSheetExpanded = MutableStateFlow(false)

    var isChangelogBottomSheetExpanded = MutableStateFlow(false)

    var bottomSheetList = MutableStateFlow(emptyList<ApkInfoModelDto>())

    var changelog = MutableStateFlow("")

    var appInfo: AppInfo by mutableStateOf(AppInfo())

    private val packageManager: PackageManagerApi by inject(PackageManagerApi::class.java)

    private val settingsRepository: SettingsRepository by inject(SettingsRepository::class.java)

    private var app: Apps? = null

    private var repository: VersionsRepository? = null

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
        appInfo = repo.appInfo
        if (repo.versionsList.isNotEmpty()) {
            list = repo.versionsList
        } else {
            coroutineScope.launch { getList(repo.appType) }
        }
    }

    suspend fun getList(appType: String) = withContext(Dispatchers.IO) {
        isRefreshing = true
        list = getVersionsListUseCase.execut(appType)
        isRefreshing = false
    }

    suspend fun showApkListBottomSheet(url: String) {
        bottomSheetList.emit(getApkListUseCase.execute(url) ?: emptyList())
        isApkListBottomSheetExpanded.emit(true)
    }

    suspend fun showChangelogBottomSheet(url: String) {
        changelog.emit(getChangelogUseCase.execut(url))
        isChangelogBottomSheetExpanded.emit(true)
    }

    fun install(file: File, installerType: Int) {
        packageManager.installApk(file, installerType)
    }

    fun delete(packageName: String) {
        packageManager.uninstall(packageName)
    }

    fun launch(packageName: String) {
        packageManager.launchApp(packageName)
    }

    fun downloadApk(
        fileName: String,
        url: String,
        isRootVersion: Boolean
    ) {
        val scope = CoroutineScope(Dispatchers.IO)

        val context: Context = get(Context::class.java)

        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "$fileName.apk")

        val fileDownloader = FileDownloader()

        scope.launch { fileDownloader.downloadFile(url, file) }

        NavBarExpandedContent.setContent { DownloadProgressContent(fileName = fileName, downloader = fileDownloader) }

        if (isRootVersion) {

        } else {
            waitDownloadAndInstall(
                status = fileDownloader.downloadStatusFlow,
                file = file
            )
        }

    }

    private fun waitDownloadAndInstall(
        status: MutableStateFlow<String>,
        file: File
    ) {
        viewModelScope.launch {
            status.collect {
                if (it == FileDownloader.END_DOWNLOAD) {
                    install(file, settingsRepository.installerType)
                }
            }
        }
    }

}