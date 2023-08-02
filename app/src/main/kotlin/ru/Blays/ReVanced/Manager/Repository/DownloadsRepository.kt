package ru.Blays.ReVanced.Manager.Repository

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import ru.Blays.ReVanced.Manager.DI.autoInject
import ru.blays.downloader.DataClass.DownloadInfo
import ru.blays.preference.DataStores.DownloadsFolderUriDS
import ru.blays.preference.DataStores.StorageAccessTypeDS
import ru.blays.revanced.shared.Data.DEFAULT_DOWNLOADS_FOLDER
import ru.blays.revanced.shared.LogManager.BLog
import ru.blays.simpledocument.SimpleDocument
import java.io.File

private const val TAG = "Downloads Repository"

class DownloadsRepository {
    
    private val storageAccessTypeDS: StorageAccessTypeDS by autoInject()
    private val downloadsFolderUriDS: DownloadsFolderUriDS by autoInject()
    private val context: Context by autoInject()

    var isDownloadRunning = mutableStateOf(false)
        private set

    var downloadsCount = mutableIntStateOf(0)
        private set

    var downloadsList = mutableStateListOf<DownloadInfo>()
        private set

    val existingFilesList = mutableStateListOf<File>()

    val existingDocumentsList = mutableStateListOf<SimpleDocument>()

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
        if(storageAccessTypeDS.value == 0) {
            val downloadDir = DEFAULT_DOWNLOADS_FOLDER
            val files = downloadDir.listFiles() ?: return
            val list = files.asList()
            existingFilesList.addAll(list)
        } else {
            try {
                val downloadDir = SimpleDocument.fromTreeUri(
                    downloadsFolderUriDS.value.toUri(),
                    context
                )
                val files = downloadDir?.documents
                files?.let { existingDocumentsList.addAll(files.filterNotNull()) }
            } catch (_: Exception) {}
        }
    }

    fun removeExistingFile(file: File) {
        existingFilesList.remove(file)
        file.delete()
    }

    fun removeExistingFile(file: SimpleDocument) {
        existingDocumentsList.remove(file)
        file.delete()
    }

    init {
        getExistingFiles()
        BLog.i(TAG, "Init downloads repository")
    }
}