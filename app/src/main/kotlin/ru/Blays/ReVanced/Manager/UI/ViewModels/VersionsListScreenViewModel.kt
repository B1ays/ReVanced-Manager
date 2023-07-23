package ru.Blays.ReVanced.Manager.UI.ViewModels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.get
import ru.Blays.ReVanced.Manager.Data.Apps
import ru.Blays.ReVanced.Manager.Data.MagiskInstallerState
import ru.Blays.ReVanced.Manager.Repository.AppRepositiry.AppRepositoryInterface
import ru.Blays.ReVanced.Manager.Repository.DownloadsRepository
import ru.Blays.ReVanced.Manager.Utils.DownloaderLogAdapter.LogAdapterBLog
import ru.Blays.ReVanced.Manager.Utils.ModuleInstallerLogAdapter.ModuleInstallerLogAdapter
import ru.blays.downloader.DownloadTask
import ru.blays.downloader.build
import ru.blays.preference.DataStores.InstallerTypeDS
import ru.blays.revanced.Elements.DataClasses.RootVersionDownloadModel
import ru.blays.revanced.Services.PublicApi.PackageManagerApi
import ru.blays.revanced.Services.Root.ModuleIntstaller.ModuleInstaller
import ru.blays.revanced.Services.Root.PackageManager.RootPackageManager
import ru.blays.revanced.domain.DataClasses.ApkInfoModelDto
import ru.blays.revanced.domain.DataClasses.VersionsInfoModelDto
import ru.blays.revanced.domain.UseCases.GetApkListUseCase
import ru.blays.revanced.domain.UseCases.GetChangelogUseCase
import ru.blays.revanced.domain.UseCases.GetVersionsListUseCase
import ru.blays.revanced.shared.Extensions.collect

class VersionsListScreenViewModel(
    private val getVersionsListUseCase: GetVersionsListUseCase,
    private val getApkListUseCase: GetApkListUseCase,
    private val getChangelogUseCase: GetChangelogUseCase,
    context: Context
) : BaseViewModel() {

    // UI states
    var isRefreshing by mutableStateOf(false)

    var appName by mutableStateOf("")

    var versionsList by mutableStateOf(emptyList<VersionsInfoModelDto>())

    var pagesCount by mutableIntStateOf(0)

    private val packageManager: PackageManagerApi = get(PackageManagerApi::class.java)
    private val downloadsRepository: DownloadsRepository = get(DownloadsRepository::class.java)

    val installerType by InstallerTypeDS(context)

    var repository: AppRepositoryInterface? = null
        private set

    fun getDataForApp(app: Apps) {
        isRefreshing = true
        val repository = app.repository
        this.repository = repository

        calculatePagesCount(repository)
        appName = repository.appName
        if (repository.remoteVersionsList.isNotEmpty()) {
            versionsList = repository.remoteVersionsList
            isRefreshing = false
        } else {
            launch { versionsList = getList(repository.appType) }
        }
    }

    private fun calculatePagesCount(repo: AppRepositoryInterface) {
        pagesCount = repo.appVersions.count()
    }

    private suspend fun getList(appType: String): List<VersionsInfoModelDto> = coroutineScope {
        isRefreshing = true
        val list = getVersionsListUseCase.execut(appType)
        isRefreshing = false
        return@coroutineScope list
    }

    fun onRefresh() {
        launch {
            repository?.appVersions?.forEach { version ->
                version.updateInfo()
            }
        }
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
            ModuleInstaller().delete(module = module)
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

        val task = DownloadTask.builder {
            this.url = url
            this.fileName = fileName
            logAdapter = LogAdapterBLog()
            onSuccess {
                file?.let { packageManager.installApk(it, installerType) }
                onRefresh()
            }
            onCancel {
                file?.delete()
            }
        }.build()
        task?.let { downloadsRepository.addToList(it) }
    }

    fun downloadRootVersion(
        filesModel: RootVersionDownloadModel,
        installCallback: (MutableStateFlow<ModuleInstaller.Status>) -> Unit
    ) {

        if (filesModel.origUrl == null) return

        val state = MutableStateFlow(MagiskInstallerState())

        val origApkDownloadTask = DownloadTask.builder {
            url = filesModel.origUrl!!
            fileName = filesModel.fileName + "-orig"
            logAdapter = LogAdapterBLog()
            onSuccess {
                launch {
                    with(state) { emit(value.copy(origApkDownloaded = true)) }

                    val installResult = async {
                        file?.let { RootPackageManager().installApp(it) }
                    }.await()

                    if (installResult?.isError == true) {
                        cancel()
                        return@launch
                    }
                    with(state) { emit(value.copy(origApkInstalled = true)) }
                }
            }
            onCancel {
                file?.delete()
            }
        }
        .build()
        .also { downloadInfo ->
            downloadInfo?.let { downloadsRepository.addToList(it) }
        }

        val modApkDownloadTask = DownloadTask.builder {
            url = filesModel.modUrl
            fileName = filesModel.fileName
            logAdapter = LogAdapterBLog()
            onSuccess {
                launch { with(state) { emit(value.copy(modApkDownloaded = true)) } }

                collect(state) { downloadState ->

                    if (downloadState.origApkInstalled) {
                        launch {
                            repository?.moduleType?.let { module ->
                                ModuleInstaller(
                                    logAdapter = ModuleInstallerLogAdapter()
                                ).also { installer ->
                                    installCallback(installer.statusFlow)
                                }.install(
                                    module,
                                    file!!
                                )
                                onRefresh()
                            }
                        }
                    }
                }
            }
            onCancel {
                file?.delete()
            }
        }
        .build()
        .also { downloadInfo ->
            downloadInfo?.let { downloadsRepository.addToList(it) }
        }
    }
}