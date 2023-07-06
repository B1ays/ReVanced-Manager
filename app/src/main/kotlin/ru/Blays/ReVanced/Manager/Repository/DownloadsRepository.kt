package ru.Blays.ReVanced.Manager.Repository

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import ru.blays.revanced.data.Downloader.DataClass.DownloadInfo
import ru.blays.revanced.data.Downloader.Utils.DEFAULT_DOWNLOADS_FOLDER
import java.io.File

class DownloadsRepository {

    var isDownloadRunning = mutableStateOf(false)
        private set

    var downloadsCount = mutableIntStateOf(0)
        private set

    var downloadsList = mutableStateListOf<DownloadInfo>()
        private set

    var existingFilesList = mutableStateListOf<File>()

    fun addToList(taskInfo: DownloadInfo) {
        downloadsList.add(taskInfo)
        downloadsCount.intValue = (downloadsCount.intValue + 1).coerceIn(0..Int.MAX_VALUE)
        isDownloadRunning.value = true
    }

    fun removeFromList(taskInfo: DownloadInfo) {
        downloadsList.remove(taskInfo)
        downloadsCount.intValue = (downloadsCount.intValue - 1).coerceIn(0..Int.MAX_VALUE)
        if (downloadsCount.intValue == 0) isDownloadRunning.value = false
    }

    private fun getExistingFiles() {
        val downloadDir = DEFAULT_DOWNLOADS_FOLDER
        val files = downloadDir.listFiles() ?: return
        val list = files.asList()
        existingFilesList.addAll(list)
    }

    fun removeExistingFile(file: File) {
        existingFilesList.remove(file)
        file.delete()
    }

    init {
        getExistingFiles()
    }
}