package ru.Blays.ReVanced.Manager.UI.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.Blays.ReVanced.Manager.Data.Apps
import ru.blays.revanced.domain.UseCases.GetVersionsListUseCase

class MainScreenViewModel(private val getVersionsListUseCase: GetVersionsListUseCase) : ViewModel(), CoroutineScope {

    override val coroutineContext = Dispatchers.IO

    var isRefreshing by mutableStateOf(false)

    @OptIn(ExperimentalStdlibApi::class)
    fun onRefresh() {
        isRefreshing = true
        launch {
            Apps.values().forEach { app ->
                app.repository.updateInfo()
            }
            isRefreshing = false
        }
    }
}