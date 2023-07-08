package ru.blays.revanced.domain.UseCases

import ru.blays.revanced.domain.Repositories.NetworkRepositoryInterface

class GetChangelogUseCase(private val networkRepositoryInterface: NetworkRepositoryInterface) {

    suspend fun execut(changelogUrl: String, recreateCache: Boolean = false) : String = networkRepositoryInterface.getText(changelogUrl, recreateCache)
}