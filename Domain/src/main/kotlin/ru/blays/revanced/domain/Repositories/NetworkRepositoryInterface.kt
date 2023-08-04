package ru.blays.revanced.domain.Repositories

import ru.blays.revanced.domain.DataClasses.ApkInfoModelDto
import ru.blays.revanced.domain.DataClasses.AppUpdateModelDto
import ru.blays.revanced.domain.DataClasses.VersionsInfoModelDto

interface NetworkRepositoryInterface {

    suspend fun getVersionsInfo(
        jsonUrl: String,
        recreateCache: Boolean
    ): List<VersionsInfoModelDto>?

    suspend fun getApkList(
        jsonUrl: String,
        recreateCache: Boolean
    ): List<ApkInfoModelDto>?

    suspend fun getManagerUpdateInfo(
        url: String,
        recreateCache: Boolean
    ): AppUpdateModelDto?

    suspend fun getText(
        changelogUrl: String,
        recreateCache: Boolean
    ): String

}