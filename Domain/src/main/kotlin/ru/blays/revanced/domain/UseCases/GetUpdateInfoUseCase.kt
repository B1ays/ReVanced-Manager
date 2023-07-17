package ru.blays.revanced.domain.UseCases

import ru.blays.revanced.domain.DataClasses.AppUpdateModelDto
import ru.blays.revanced.domain.Repositories.NetworkRepositoryInterface

class GetUpdateInfoUseCase(private val networkRepositoryInterface: NetworkRepositoryInterface) {

    suspend fun execute(url: String): AppUpdateModelDto? {
        return networkRepositoryInterface.getManagerUpdateInfo(url)
    }

}