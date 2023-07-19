package ru.Blays.ReVanced.Manager.UI.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import ru.Blays.ReVanced.Manager.Data.Apps
import ru.blays.revanced.domain.UseCases.GetVersionsListUseCase

class MainScreenViewModel(private val getVersionsListUseCase: GetVersionsListUseCase) : BaseViewModel() {

    var isRefreshing by mutableStateOf(false)

    fun onRefresh() {
        isRefreshing = true
        launch {
            Apps.entries.forEach { app ->
                app.repository.updateInfo(recreateCache = true)
            }
            isRefreshing = false
        }
    }

    init {
        onRefresh()
    }
}