package ru.blays.revanced.domain.UseCases

import ru.blays.revanced.domain.DataClasses.VersionsInfoModelDto
import ru.blays.revanced.domain.Repositories.NetworkRepositoryInterface

class GetVersionsListUseCase(private val networkRepositoryInterface: NetworkRepositoryInterface) {
    suspend fun execut(url: String, recreateCache: Boolean = false): List<VersionsInfoModelDto> {
        return networkRepositoryInterface.getVersionsInfo(url, recreateCache) ?: emptyList()
    }
}