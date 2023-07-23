package ru.blays.downloader.DataClass

import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File

data class DownloadInfo(
    val fileName: String,
    val file: File,
    val progressFlow: MutableStateFlow<Float>,
    val speedFlow: MutableStateFlow<Long>,
    val actionPauseResume: () -> Unit,
    val actionCancel: () -> Unit
)