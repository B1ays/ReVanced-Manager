package ru.blays.revanced.domain.UseCases

import ru.blays.revanced.domain.Repositories.AppInfoRepositoryInterface

class GetChangelogUseCase(private val appInfoRepositoryInterface: AppInfoRepositoryInterface) {

    suspend fun execut(changelogUrl: String) : String = appInfoRepositoryInterface.getChangelog(changelogUrl)
}