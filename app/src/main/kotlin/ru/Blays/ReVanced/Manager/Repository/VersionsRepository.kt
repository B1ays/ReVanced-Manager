package ru.Blays.ReVanced.Manager.Repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import ru.blays.revanced.Elements.DataClasses.AppInfo
import ru.blays.revanced.Services.PublicApi.PackageManagerApi
import ru.blays.revanced.Services.RootService.Util.MagiskInstaller
import ru.blays.revanced.domain.DataClasses.VersionsInfoModelDto
import ru.blays.revanced.domain.UseCases.GetVersionsListUseCase

abstract class VersionsRepository : CoroutineScope {

    // CoroutineScope for launch suspend functions
    override val coroutineContext = Dispatchers.IO

    // Info about application
    var version: MutableStateFlow<String?> = MutableStateFlow(null)

    val nonRootVersion: MutableStateFlow<String?> = MutableStateFlow(null)

    val rootVersion: MutableStateFlow<String?> = MutableStateFlow(null)

    var patchesVersion: MutableStateFlow<String?> = MutableStateFlow(null)

    var availableVersion: MutableStateFlow<String?> = MutableStateFlow(null)

    var availablePatchesVersion: MutableStateFlow<String?> = MutableStateFlow(null)

    var versionsList = emptyList<VersionsInfoModelDto>()

    abstract val appName: String

    abstract val packageName: String

    open val nonRootPackageName: String? = null

    abstract val appType: String

    open val moduleType: MagiskInstaller.Module? = null

    open val isModuleInstalled: MutableStateFlow<Boolean>? = null
    open val isNonRootVersionInstalled: MutableStateFlow<Boolean>? = null

    abstract val hasRootVersion: Boolean

    // Package Manager by DI inject
    private val packageManager: PackageManagerApi by inject(PackageManagerApi::class.java)

    // function what used to get info about app
    suspend fun getAvailableVersions(getVersionsListUseCase: GetVersionsListUseCase, recreateCache: Boolean) {
        versionsList = getVersionsListUseCase.execut(appType, recreateCache)
        with(versionsList.firstOrNull()) {
            availableVersion.emit(this?.version)
            availablePatchesVersion.emit(this?.patchesVersion)
        }
    }

    suspend fun getLocalVersions() {
        if (hasRootVersion) {
            if (isModuleInstalled?.value == true) rootVersion.value = packageManager.getVersionName(packageName)
                .await()
                .getValueOrNull()
            if (isNonRootVersionInstalled?.value == true) nonRootVersion.value = nonRootPackageName?.let {
                packageManager.getVersionName(it)
                    .await()
                    .getValueOrNull()
            }
        } else {
            version.value = packageManager.getVersionName(packageName)
                .await()
                .getValueOrNull()
        }
    }

    suspend fun checkNonRootVersionExist(packageName: String): Boolean {
        val result = packageManager.getVersionName(packageName).await()
        return result.isSuccess
    }

    fun generateAppInfo(isRootVersion: Boolean = false): AppInfo {
        return if (isRootVersion && hasRootVersion) AppInfo(
            appName = appName,
            version = rootVersion,
            patchesVersion = patchesVersion,
            packageName = packageName
        ) else if (!isRootVersion && hasRootVersion) AppInfo(
            appName = appName,
            version = nonRootVersion,
            patchesVersion = patchesVersion,
            packageName = nonRootPackageName
        )
        else if (!isRootVersion && !hasRootVersion) AppInfo(
            appName,
            version,
            patchesVersion,
            packageName
        ) else AppInfo()
    }

    abstract suspend fun updateInfo(recreateCache: Boolean)
}

class YoutubeVersionsRepository(private val getVersionsListUseCase: GetVersionsListUseCase) : VersionsRepository() {

    override val appType = GetVersionsListUseCase.YOUTUBE
    override val appName = "YouTube ReVanced"
    override val packageName = "com.google.android.youtube"
    override val nonRootPackageName = "app.revanced.android.youtube"
    override val moduleType = MagiskInstaller.Module.YOUTUBE

    override val hasRootVersion = true

    override val isModuleInstalled = MutableStateFlow(false)
    override val isNonRootVersionInstalled = MutableStateFlow(false)

    override suspend fun updateInfo(recreateCache: Boolean) {
        getAvailableVersions(getVersionsListUseCase, recreateCache)
        getLocalVersions()
    }

    init {
        launch {
            isModuleInstalled.value = MagiskInstaller.checkModuleExist(moduleType)
            isNonRootVersionInstalled.value = checkNonRootVersionExist(nonRootPackageName)
        }
    }
}

class YoutubeMusicVersionsRepository(private val getVersionsListUseCase: GetVersionsListUseCase) : VersionsRepository() {

    override val appType = GetVersionsListUseCase.MUSIC
    override val appName = "YouTubeMusic ReVanced"
    override val packageName = "com.google.android.apps.youtube.music"
    override val nonRootPackageName = "app.revanced.android.apps.youtube.music"
    override val moduleType = MagiskInstaller.Module.YOUTUBE_MUSIC

    override val hasRootVersion = true

    override val isModuleInstalled = MutableStateFlow(false)
    override val isNonRootVersionInstalled = MutableStateFlow(false)

    override suspend fun updateInfo(recreateCache: Boolean) {
        getAvailableVersions(getVersionsListUseCase, recreateCache)
        getLocalVersions()
    }

    init {
        launch {
            isModuleInstalled.value = MagiskInstaller.checkModuleExist(moduleType)
            isNonRootVersionInstalled.value = checkNonRootVersionExist(nonRootPackageName)
        }
    }
}

class MicroGVersionsRepository(private val getVersionsListUseCase: GetVersionsListUseCase) : VersionsRepository() {

    override val appType = GetVersionsListUseCase.MICROG
    override val appName = "ReVanced MicroG"
    override val packageName = "com.mgoogle.android.gms"

    override val hasRootVersion = false

    override suspend fun updateInfo(recreateCache: Boolean) {
        getAvailableVersions(getVersionsListUseCase, recreateCache)
        getLocalVersions()
    }
}