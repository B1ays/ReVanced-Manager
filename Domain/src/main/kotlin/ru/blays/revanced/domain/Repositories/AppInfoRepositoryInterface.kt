package ru.blays.revanced.domain.Repositories

import ru.blays.revanced.domain.DataClasses.ApkInfoModelDto
import ru.blays.revanced.domain.DataClasses.VersionsInfoModelDto

interface AppInfoRepositoryInterface {

    suspend fun getVersionsInfo(jsonUrl: String): List<VersionsInfoModelDto>?

    suspend fun getApkList(jsonUrl: String): List<ApkInfoModelDto>?
}