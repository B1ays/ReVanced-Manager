package ru.Blays.ReVanced.Manager.Repository.AppRepositiry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

data class AppVersionModel (
    var versionName: String? = null,
    var packageName: String? = null,
    var isRootNeeded: Boolean = false,
    var localVersionSource: (suspend () -> String?)? = null,
    var remoteVersionSource: (suspend () -> String?)? = null,

    ) {
    private val _localVersionName: MutableStateFlow<String?> = MutableStateFlow(null)
    private val _remoteVersionName: MutableStateFlow<String?> = MutableStateFlow(null)
    private val _patchesVersion: MutableStateFlow<String?> = MutableStateFlow(null)

    val localVersionNameState: State<String?>
        @Composable get() = _localVersionName.collectAsState()

    val localVersionName: String?
        get() = _localVersionName.value

    val remoteVersionNameState: State<String?>
        @Composable get() = _remoteVersionName.collectAsState()

    val remoteVersionName: String?
        get() = _remoteVersionName.value

    val patchesVersionState: State<String?>
        @Composable get() = _patchesVersion.collectAsState()

    val patchesVersion: String?
        get() = _patchesVersion.value

    private val scope = CoroutineScope(Dispatchers.IO)

    suspend fun updateInfo() {
        coroutineScope {
            localVersionSource?.let { _localVersionName.value = it.invoke() }
        }
        coroutineScope {
            remoteVersionSource?.let { _remoteVersionName.value = it.invoke() }
        }
    }

    init {
        scope.launch {
           updateInfo()
        }
    }
}