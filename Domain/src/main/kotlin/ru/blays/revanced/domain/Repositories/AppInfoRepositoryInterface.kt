package ru.blays.revanced.domain.Repositories

import ru.blays.revanced.domain.DataClasses.ApkInfoModelDto
import ru.blays.revanced.domain.DataClasses.VersionsInfoModelDto

interface AppInfoRepositoryInterface {

    suspend fun getVersionsInfo(
        jsonUrl: String,
        recreateCache: Boolean
    ): List<VersionsInfoModelDto>?

    suspend fun getApkList(jsonUrl: String, recreateCache: Boolean): List<ApkInfoModelDto>?
    suspend fun getChangelog(changelogUrl: String, recreateCache: Boolean): String
}