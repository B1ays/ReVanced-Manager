package ru.blays.revanced.domain.UseCases

import ru.blays.revanced.domain.DataClasses.VersionsInfoModelDto
import ru.blays.revanced.domain.Repositories.AppInfoRepositoryInterface

class GetVersionsListUseCase(private val appInfoRepositoryInterface: AppInfoRepositoryInterface) {

    companion object {
        const val YOUTUBE = "YouTube"
        const val MUSIC = "Music"

        const val YOUTUBE_JSON_LINK = "https://raw.githubusercontent.com/B1ays/RVMusicModule/main/versionsInfo.json"
        const val MUSIC_JSON_LINK = ""
    }

    suspend fun execut(appType: String) : List<VersionsInfoModelDto> = when (appType) {

            YOUTUBE -> appInfoRepositoryInterface.getVersionsInfo(YOUTUBE_JSON_LINK) ?: emptyList()

            MUSIC -> appInfoRepositoryInterface.getVersionsInfo(MUSIC_JSON_LINK) ?: emptyList()

            else -> emptyList()
        }
}