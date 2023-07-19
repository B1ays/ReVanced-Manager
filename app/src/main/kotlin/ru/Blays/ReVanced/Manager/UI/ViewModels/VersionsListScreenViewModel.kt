package ru.Blays.ReVanced.Manager.UI.ViewModels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.topjohnwu.superuser.Shell
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
import ru.Blays.ReVanced.Manager.Repository.DownloadsRepository
import ru.Blays.ReVanced.Manager.Repository.SettingsRepository
import ru.Blays.ReVanced.Manager.Repository.VersionsRepository
import ru.Blays.ReVanced.Manager.Utils.DownloaderLogAdapter.LogAdapterBLog
import ru.blays.revanced.Elements.DataClasses.RootVersionDownloadModel
import ru.blays.revanced.Services.PublicApi.PackageManagerApi
import ru.blays.revanced.Services.RootService.PackageManager.RootPackageManager
import ru.blays.revanced.Services.RootService.Util.MagiskInstaller
import ru.blays.revanced.Services.RootService.Util.isRootGranted
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
) : BaseViewModel() {

    // UI states
    var isRefreshing by mutableStateOf(false)

    var appName by mutableStateOf("")

    var versionsList by mutableStateOf(emptyList<VersionsInfoModelDto>())

    var pagesCount by mutableIntStateOf(0)

    private val packageManager: PackageManagerApi = get(PackageManagerApi::class.java)

    private val settingsRepository: SettingsRepository = get(SettingsRepository::class.java)

    private val downloadsRepository: DownloadsRepository = get(DownloadsRepository::class.java)

    var repository: VersionsRepository? = null
        private set

    fun getDataForApp(app: Apps) {
        isRefreshing = true
        val repository = app.repository
        this.repository = repository

        calculatePagesCount(repository)
        appName = repository.appName
        if (repository.versionsList.isNotEmpty()) {
            versionsList = repository.versionsList
            isRefreshing = false
        } else {
            launch { getList(repository.appType) }
        }
    }

    private fun calculatePagesCount(repo: VersionsRepository) {
        pagesCount = if (repo.hasRootVersion && isRootGranted) 2 else 1
    }

    private suspend fun getList(appType: String) = withContext(Dispatchers.IO) {
        isRefreshing = true
        versionsList = getVersionsListUseCase.execut(appType)
        isRefreshing = false
    }

    fun onRefresh() {
        launch { repository?.updateInfo(recreateCache = true) }
    }

    suspend fun getApkList(url: String, rootVersion: Boolean): List<ApkInfoModelDto> {
         return getApkListUseCase.execute(url)
            ?.filter { it.isRootVersion == rootVersion }
            ?: emptyList()
    }

    suspend fun getChangelog(url: String): String {
        return getChangelogUseCase.execut(url)
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
                    file?.let { packageManager.installApk(it, settingsRepository.installerType) }
                    onRefresh()
                },
                onCancel = {
                    file?.delete()
                }
            )
            .setLogAdapter(LogAdapterBLog::class)
            .build()

        task?.let { downloadsRepository.addToList(it) }
    }

    fun downloadRootVersion(
        filesModel: RootVersionDownloadModel
    ) {

        if (filesModel.origUrl == null) return

        val context: Context by inject(Context::class.java)

        val state = MutableStateFlow(MagiskInstallerState())

        val origApkDownloadTask = DownloadTask(url = filesModel.origUrl!!, fileName = filesModel.fileName + "-orig")
            .setDefaultActions(
                onSuccess = {
                    launch {
                        with(state) { emit(value.copy(origApkDownloaded = true)) }

                        val installResult = async {
                            file?.let { RootPackageManager().installApp(it) }
                        }.await()

                        if (installResult?.isError == true) {
                            this.cancel()
                            return@launch
                        }
                        with(state) { emit(value.copy(origApkInstalled = true)) }
                    }
                },
                onCancel = {
                    file?.delete()
                }
            )
            .setLogAdapter(LogAdapterBLog::class)
            .build()
            .also { downloadInfo ->
                downloadInfo?.let { downloadsRepository.addToList(it) }
            }

        val modApkDownloadTask = DownloadTask(url = filesModel.modUrl, fileName = filesModel.fileName)
            .setDefaultActions(
                onSuccess = {
                    launch { with(state) { emit(value.copy(modApkDownloaded = true)) } }

                    collect(state) {

                        if (it.origApkInstalled) {
                            launch {
                                repository?.moduleType?.let { module ->
                                    MagiskInstaller.install(
                                        module,
                                        file!!,
                                        context
                                    )
                                    onRefresh()
                                }
                            }
                        }
                    }
                },
                onCancel = {
                    file?.delete()
                }
            )
            .setLogAdapter(LogAdapterBLog::class)
            .build()
            .also { downloadInfo ->
                downloadInfo?.let { downloadsRepository.addToList(it) }
            }
    }
}