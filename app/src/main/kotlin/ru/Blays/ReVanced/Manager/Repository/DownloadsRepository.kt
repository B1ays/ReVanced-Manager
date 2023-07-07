package ru.Blays.ReVanced.Manager.Repository

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import ru.blays.revanced.data.Downloader.DataClass.DownloadInfo
import ru.blays.revanced.data.Downloader.Utils.DEFAULT_DOWNLOADS_FOLDER
import ru.blays.revanced.shared.LogManager.BLog
import java.io.File

private const val TAG = "Downloads Repository"

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
        BLog.i(TAG, "Add task to list: ${taskInfo.fileName}")
    }

    fun removeFromList(taskInfo: DownloadInfo) {
        downloadsList.remove(taskInfo)
        downloadsCount.intValue = (downloadsCount.intValue - 1).coerceIn(0..Int.MAX_VALUE)
        if (downloadsCount.intValue == 0) isDownloadRunning.value = false
        BLog.i(TAG, "Remove download from list: ${taskInfo.fileName}")
    }

    private fun getExistingFiles() {
        val downloadDir = DEFAULT_DOWNLOADS_FOLDER
        val files = downloadDir.listFiles() ?: return
        val list = files.asList()
        existingFilesList.addAll(list)
        BLog.i(TAG, "Get existing downloaded files. Count: ${list.count()}")
    }

    fun removeExistingFile(file: File) {
        existingFilesList.remove(file)
        file.delete()
    }

    init {
        getExistingFiles()
        BLog.i(TAG, "Init downloads repository")
    }
}