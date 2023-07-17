package ru.blays.revanced.domain.UseCases

import ru.blays.revanced.domain.DataClasses.VersionsInfoModelDto
import ru.blays.revanced.domain.Repositories.NetworkRepositoryInterface

class GetVersionsListUseCase(private val networkRepositoryInterface: NetworkRepositoryInterface) {

    companion object {
        const val YOUTUBE = "YouTube"
        const val MUSIC = "Music"
        const val MICROG = "MicroG"

        private const val YOUTUBE_JSON_LINK = "https://github.com/B1ays/ReVanced-Versions-Catalog/raw/main/YouTube/VersionsList.json"
        private const val MUSIC_JSON_LINK = "https://github.com/B1ays/ReVanced-Versions-Catalog/raw/main/YouTube%20Music/VersionsList.json"
        private const val MICROG_JSON_LINK = "https://github.com/B1ays/ReVanced-Versions-Catalog/raw/main/MicroG/VersionsList.json"
    }

    suspend fun execut(appType: String, recreateCache: Boolean = false) : List<VersionsInfoModelDto> = when (appType) {

        YOUTUBE -> networkRepositoryInterface.getVersionsInfo(YOUTUBE_JSON_LINK, recreateCache) ?: emptyList()

        MUSIC -> networkRepositoryInterface.getVersionsInfo(MUSIC_JSON_LINK, recreateCache) ?: emptyList()

        MICROG -> networkRepositoryInterface.getVersionsInfo(MICROG_JSON_LINK, recreateCache) ?: emptyList()

        else -> emptyList()
    }
}