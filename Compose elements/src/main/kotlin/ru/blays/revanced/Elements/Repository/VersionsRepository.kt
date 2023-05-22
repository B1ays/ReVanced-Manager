package ru.blays.revanced.Elements.Repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import ru.blays.revanced.Elements.DataClasses.AppInfo
import ru.blays.revanced.Services.PublicApi.PackageManagerApi
import ru.blays.revanced.domain.DataClasses.VersionsInfoModelDto
import ru.blays.revanced.domain.UseCases.GetVersionsListUseCase

abstract class VersionsRepository : CoroutineScope {

    // CoroutineScope for launch suspend functions
    override val coroutineContext = Dispatchers.IO

    // Info about application
    var version: MutableStateFlow<String?> = MutableStateFlow(null)

    var patchesVersion: MutableStateFlow<String?> = MutableStateFlow(null)

    var availableVersion: MutableStateFlow<String?> = MutableStateFlow(null)

    var availablePatchesVersion: MutableStateFlow<String?> = MutableStateFlow(null)

    var versionsList = emptyList<VersionsInfoModelDto>()

    abstract val appName: String

    abstract val packageName: String

    abstract val appType: String

    // AppInfo object
    val appInfo by lazy {
        AppInfo(
            appName,
            version.value,
            patchesVersion.value,
            packageName
        )
    }

    // Package Manager by DI inject
    private val packageManager: PackageManagerApi by inject(PackageManagerApi::class.java)

    // function what used to get info about app
    suspend fun getAvailableVersions(getVersionsListUseCase: GetVersionsListUseCase) {
        versionsList = getVersionsListUseCase.execut(appType)
        with(versionsList.firstOrNull()) {
            availableVersion.emit(this?.version)
            availablePatchesVersion.emit(this?.patchesVersion)
        }
    }

    suspend fun getLocalVersions() {
        version.emit(packageManager.getVersionName(packageName).await().getValueOrNull())
    }
}

class YoutubeVersionsRepository(private val getVersionsListUseCase: GetVersionsListUseCase) : VersionsRepository() {

    override val appType = GetVersionsListUseCase.YOUTUBE
    override val appName = "YouTube ReVanced"
    override val packageName = "com.google.android.youtube"

    init {
        launch {
            getAvailableVersions(getVersionsListUseCase)
            getLocalVersions()
        }
    }
}

class YoutubeMusicVersionsRepository(private val getVersionsListUseCase: GetVersionsListUseCase) : VersionsRepository() {

    override val appType = GetVersionsListUseCase.MUSIC
    override val appName = "YouTubeMusic ReVanced"
    override val packageName = "com.google.android.apps.youtube.music"

    init {
        launch {
            getAvailableVersions(getVersionsListUseCase)
            getLocalVersions()
        }
    }
}

class MicroGVersionsRepository(private val getVersionsListUseCase: GetVersionsListUseCase) : VersionsRepository() {

    override val appType = GetVersionsListUseCase.MICROG
    override val appName = "ReVanced MicroG"
    override val packageName = "com.mgoogle.android.gms"

    init {
        launch {
            getAvailableVersions(getVersionsListUseCase)
            getLocalVersions()
        }
    }
}