package ru.Blays.ReVanced.Manager.UI.ViewModels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.Blays.ReVanced.Manager.DI.autoInject
import ru.Blays.ReVanced.Manager.Data.Apps
import ru.Blays.ReVanced.Manager.Data.MagiskInstallerState
import ru.Blays.ReVanced.Manager.Repository.AppRepositiry.AppRepositoryInterface
import ru.Blays.ReVanced.Manager.Repository.DownloadsRepository
import ru.Blays.ReVanced.Manager.Utils.DownloaderLogAdapter.LogAdapterBLog
import ru.Blays.ReVanced.Manager.Utils.ModuleInstallerLogAdapter.ModuleInstallerLogAdapter
import ru.blays.downloader.DataClass.StorageMode
import ru.blays.downloader.DownloadTask
import ru.blays.downloader.build
import ru.blays.preference.DataStores.DownloadsFolderUriDS
import ru.blays.preference.DataStores.InstallerTypeDS
import ru.blays.preference.DataStores.StorageAccessTypeDS
import ru.blays.revanced.Elements.DataClasses.RootVersionDownloadModel
import ru.blays.revanced.DeviceUtils.PublicApi.PackageManagerApi
import ru.blays.revanced.DeviceUtils.Root.ModuleIntstaller.ModuleInstaller
import ru.blays.revanced.DeviceUtils.Root.PackageManager.RootPackageManager
import ru.blays.revanced.domain.DataClasses.ApkInfoModelDto
import ru.blays.revanced.domain.DataClasses.VersionsInfoModelDto
import ru.blays.revanced.domain.UseCases.GetApkListUseCase
import ru.blays.revanced.domain.UseCases.GetChangelogUseCase
import ru.blays.revanced.domain.UseCases.GetVersionsListUseCase
import ru.blays.revanced.shared.Data.APK_FILE_EXTENSION
import ru.blays.revanced.shared.Data.APK_MIME_TYPE
import ru.blays.revanced.shared.Data.DEFAULT_INSTALLER_CACHE_FOLDER
import ru.blays.revanced.shared.Extensions.collect
import ru.blays.revanced.shared.LogManager.BLog
import ru.blays.simpledocument.SimpleDocument
import java.io.File

class VersionsListScreenViewModel(
    private val getVersionsListUseCase: GetVersionsListUseCase,
    private val getApkListUseCase: GetApkListUseCase,
    private val getChangelogUseCase: GetChangelogUseCase,
    private val context: Context
) : BaseViewModel() {

    // UI states
    var isRefreshing by mutableStateOf(false)

    var appName by mutableStateOf("")

    var versionsList by mutableStateOf(emptyList<VersionsInfoModelDto>())

    var pagesCount by mutableIntStateOf(0)

    private val packageManager: PackageManagerApi by autoInject()
    private val downloadsRepository: DownloadsRepository by autoInject()

    private val _downloadsFolderUri: DownloadsFolderUriDS by autoInject()
    private val _storageMode: StorageAccessTypeDS  by autoInject()
    private val _installerType: InstallerTypeDS by autoInject()
    private var downloadsFolderUri by _downloadsFolderUri
    private var storageMode by _storageMode
    private var installerType by _installerType

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
            launch { versionsList = getList(repository.catalogUrl) }
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
        DownloadTask.builder {
            this.url = url
            this.fileName = fileName
            logAdapter = LogAdapterBLog()
            when(this@VersionsListScreenViewModel.storageMode) {
                0 -> {
                    storageMode = StorageMode.FileIO
                    onSuccess {
                        file?.let { packageManager.installApk(it, installerType) }
                        onRefresh()
                    }
                    onCancel {
                        file?.delete()
                    }
                }
                1 -> {
                    simpleDocument = SimpleDocument.fromTreeUri(
                        downloadsFolderUri.toUri(),
                        context
                    )
                    ?.getOrCreateDocument(
                        fileName,
                        APK_FILE_EXTENSION,
                        APK_MIME_TYPE
                    )
                    storageMode = StorageMode.SAF
                    logAdapter = LogAdapterBLog()
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
                            packageManager.installApk(tmpFile, installerType)
                            onRefresh()
                        }
                    }
                    onCancel {
                        simpleDocument?.delete()
                    }
                }
            }
        }
        .build()
        .also {
            it?.let { downloadsRepository.addToList(it) }
        }
    }

    fun downloadRootVersion(
        filesModel: RootVersionDownloadModel,
        installCallback: (MutableStateFlow<ModuleInstaller.Status>) -> Unit
    ) {

        if (filesModel.origUrl == null) {
            BLog.d("Download", "origUrl is null")
            return
        }

        val state = MutableStateFlow(MagiskInstallerState())

        DownloadTask.builder {
            url = filesModel.origUrl!!
            fileName = filesModel.fileName + "-orig"
            logAdapter = LogAdapterBLog()
            when(this@VersionsListScreenViewModel.storageMode) {
                0 -> {
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
                1 -> {
                    simpleDocument = SimpleDocument.fromTreeUri(
                        downloadsFolderUri.toUri(),
                        context
                    )
                    ?.getOrCreateDocument(
                        fileName,
                        APK_FILE_EXTENSION,
                        APK_MIME_TYPE
                    )
                    storageMode = StorageMode.SAF
                    onSuccess {
                        launch {
                            with(state) { emit(value.copy(origApkDownloaded = true)) }
                            val tmpFile = File(
                                DEFAULT_INSTALLER_CACHE_FOLDER(context),
                                fileName + APK_FILE_EXTENSION
                            ).apply {
                                if (!exists()) createNewFile()
                            }
                            val copyToTemp = simpleDocument!!.copyTo(tmpFile)
                            if (!copyToTemp) return@launch

                            val installResult = async {
                                 RootPackageManager().installApp(tmpFile)
                            }.await()

                            if (installResult.isError) {
                                cancel()
                                return@launch
                            }
                            with(state) { emit(value.copy(origApkInstalled = true)) }
                        }
                    }
                    onCancel {
                        simpleDocument?.delete()
                    }
                }
            }
        }
        .build()
        .also { downloadInfo ->
            downloadInfo?.let { downloadsRepository.addToList(it) }
        }

        DownloadTask.builder {
            url = filesModel.modUrl
            fileName = filesModel.fileName
            logAdapter = LogAdapterBLog()

            when(this@VersionsListScreenViewModel.storageMode) {
                0 -> {
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
                1 -> {
                    simpleDocument = SimpleDocument.fromTreeUri(
                        downloadsFolderUri.toUri(),
                        context
                    )
                    ?.getOrCreateDocument(
                        fileName,
                        APK_FILE_EXTENSION,
                        APK_MIME_TYPE
                    )
                    storageMode = StorageMode.SAF
                    onSuccess {
                        launch { with(state) { emit(value.copy(modApkDownloaded = true)) } }
                        collect(state) { downloadState ->
                            if (downloadState.origApkInstalled) {
                                launch {
                                    val tmpFile = File(
                                        DEFAULT_INSTALLER_CACHE_FOLDER(context),
                                        fileName + APK_FILE_EXTENSION
                                    ).apply {
                                        if (!exists()) createNewFile()
                                    }
                                    val copyToTemp = simpleDocument!!.copyTo(tmpFile)
                                    if (!copyToTemp) return@launch
                                    repository?.moduleType?.let { module ->
                                        ModuleInstaller(
                                            logAdapter = ModuleInstallerLogAdapter()
                                        ).also { installer ->
                                            installCallback(installer.statusFlow)
                                        }.install(
                                            module,
                                            tmpFile
                                        )
                                        onRefresh()
                                    }
                                }
                            }
                        }
                    }
                    onCancel {
                        simpleDocument?.delete()
                    }
                }
            }
        }
        .build()
        .also { downloadInfo ->
            downloadInfo?.let { downloadsRepository.addToList(it) }
        }
    }
}