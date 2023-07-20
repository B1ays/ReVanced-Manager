package ru.Blays.ReVanced.Manager.UI.ViewModels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.Blays.ReVanced.Manager.Data.Apps

class MainScreenViewModel : BaseViewModel() {

    private val _isRefreshing = MutableStateFlow(false)

    val isRefreshing: Boolean
        @Composable get() = _isRefreshing.collectAsState().value

    fun onRefresh() {
        _isRefreshing.value = true
        launch {
            Apps.entries.forEach { app ->
                app.repository.appVersions.forEach { version ->
                    version.updateInfo()
                }
            }
            _isRefreshing.value = false
        }
    }
}