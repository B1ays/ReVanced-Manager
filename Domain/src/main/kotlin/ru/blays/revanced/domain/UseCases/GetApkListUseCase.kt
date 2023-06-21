package ru.blays.revanced.domain.UseCases

import ru.blays.revanced.domain.DataClasses.ApkInfoModelDto
import ru.blays.revanced.domain.Repositories.AppInfoRepositoryInterface

class GetApkListUseCase(private val appInfoRepositoryInterface: AppInfoRepositoryInterface) {

    suspend fun execute(url: String, recreateCache: Boolean = false) : List<ApkInfoModelDto>? = appInfoRepositoryInterface.getApkList(url, recreateCache)
}