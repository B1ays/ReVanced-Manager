package ru.blays.revanced.Presentation.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.blays.revanced.data.repositories.AppInfoRepositoryImplementation
import ru.blays.revanced.domain.DataClasses.VersionsInfoModelDto
import ru.blays.revanced.domain.UseCases.GetVersionsListUseCase

class VersionsListScreenViewModel(
    private val getVersionsListUseCase: GetVersionsListUseCase
) : ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val appInfoRepositoryImpl = AppInfoRepositoryImplementation()
                val getVersionsListUseCase = GetVersionsListUseCase(appInfoRepositoryImpl)
                VersionsListScreenViewModel(getVersionsListUseCase)
            }
        }

        const val YOUTUBE = "YouTube"
        const val MUSIC = "Music"
    }

    var isRefreshing by mutableStateOf(false)


    var list by mutableStateOf(emptyList<VersionsInfoModelDto>())

    suspend fun getList(appType: String) = withContext(Dispatchers.IO) {
        isRefreshing = true
        list = getVersionsListUseCase.execut(appType)
        isRefreshing = false
    }
}