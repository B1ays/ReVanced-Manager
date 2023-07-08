package ru.blays.revanced.domain.UseCases

import ru.blays.revanced.domain.DataClasses.ApkInfoModelDto
import ru.blays.revanced.domain.Repositories.NetworkRepositoryInterface

class GetApkListUseCase(private val networkRepositoryInterface: NetworkRepositoryInterface) {

    suspend fun execute(url: String, recreateCache: Boolean = false) : List<ApkInfoModelDto>? = networkRepositoryInterface.getApkList(url, recreateCache)
}