package ru.Blays.ReVanced.Manager.Repository

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import ru.Blays.ReVanced.Manager.DI.autoInject
import ru.blays.downloader.DataClass.DownloadInfo
import ru.blays.preference.DataStores.DownloadsFolderUriDS
import ru.blays.preference.DataStores.StorageAccessTypeDS
import ru.blays.revanced.shared.Data.DEFAULT_DOWNLOADS_FOLDER
import ru.blays.revanced.shared.LogManager.BLog
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

    val existingDocumentsList = mutableStateListOf<DocumentFile>()

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
            val downloadDir = DocumentFile.fromTreeUri(
                context,
                downloadsFolderUriDS.value.toUri()
            )
            val files = downloadDir?.listFiles()
            files?.let { existingDocumentsList.addAll(it) }
        }
    }

    fun removeExistingFile(file: File) {
        existingFilesList.remove(file)
        file.delete()
    }

    fun removeExistingFile(file: DocumentFile) {
        existingDocumentsList.remove(file)
        file.delete()
    }

    init {
        getExistingFiles()
        BLog.i(TAG, "Init downloads repository")
    }
}