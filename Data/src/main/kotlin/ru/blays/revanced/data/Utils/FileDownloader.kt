package ru.blays.revanced.data.Utils

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import java.io.File
import java.io.FileOutputStream

interface FileDownloaderInterface {

    fun downloadFile(model: FileDownloadDto): DownloadState

    suspend fun downloadFiles(files: List<FileDownloadDto>): DownloadState

}

data class FileDownloadDto(
    val url: String,
    val file: File
)

data class DownloadState(
    val downloadStatusFlow: MutableStateFlow<String>,
    val progressFlow: MutableStateFlow<Float>,
    val fileNameFlow: MutableStateFlow<String>
)

class FileDownloader : FileDownloaderInterface {

    companion object {
        const val START_REQUEST = "START_REQUEST"
        const val START_DOWNLOAD = "START_DOWNLOAD"
        const val END_DOWNLOAD = "END_DOWNLOAD"
        const val ERROR = "ERROR"
    }

    private val job = CoroutineScope(Dispatchers.IO)

    lateinit var file: File

    override fun downloadFile(model: FileDownloadDto): DownloadState {

        val downloadStatusFlow = MutableStateFlow("")

        val progressFlow = MutableStateFlow(0F)

        val fileNameFlow = MutableStateFlow(model.file.name)

        val client = OkHttpClient()

        this.file = model.file

        job.launch {

            try {

                val request = Request.Builder()
                    .url(url = model.url)
                    .build()

                downloadStatusFlow.tryEmit(START_REQUEST)

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IOException(
                            "Запрос к серверу не был успешен:" +
                                    " ${response.code} ${response.message}"
                        )
                    }

                    if (model.file.exists()) {
                        downloadStatusFlow.tryEmit(END_DOWNLOAD)
                        Log.i("Downloader", "file already exists")
                        job.cancel()
                        return@launch
                    }

                    downloadStatusFlow.tryEmit(START_DOWNLOAD)

                    val inputStream = response.body.byteStream()
                    val outputStream = FileOutputStream(model.file)


                    val fileSize = response.body.contentLength()

                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    var totalBytesRead: Long = 0

                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead
                        val progress: Float = (totalBytesRead.toFloat() / fileSize.toFloat())
                        progressFlow.emit(progress)
                    }
                    inputStream.close()
                    outputStream.close()
                    downloadStatusFlow.tryEmit(END_DOWNLOAD)
                }
            } catch (e: IOException) {
                downloadStatusFlow.tryEmit(ERROR)
                Log.w("Downloader", "Ошибка подключения: $e")
                job.cancel()
            }
        }
        return DownloadState(downloadStatusFlow, progressFlow, fileNameFlow)
    }

    override suspend fun downloadFiles(files: List<FileDownloadDto>): DownloadState {

        val downloadStatusFlow = MutableStateFlow("")

        val progressFlow = MutableStateFlow(0F)

        val fileNameFlow = MutableStateFlow("")

        val client = OkHttpClient()

        job.launch {

            for (item in files) {

                progressFlow.emit(0F)

                fileNameFlow.emit(item.file.name)

                try {

                    val request = Request.Builder()
                        .url(url = item.url)
                        .build()

                    downloadStatusFlow.tryEmit(START_REQUEST)

                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) {
                            throw IOException(
                                "Запрос к серверу не был успешен:" +
                                        " ${response.code} ${response.message}"
                            )
                        }

                        if (item.file.exists()) {
                            Log.i("Downloader", "item already exists")
                            job.cancel()
                            throw (FileAlreadyExistsException(file))
                        }

                        Log.i("Downloader", "download update")

                        downloadStatusFlow.tryEmit(START_DOWNLOAD)

                        val inputStream = response.body.byteStream()
                        val outputStream = FileOutputStream(item.file)

                        val fileSize = response.body.contentLength()

                        val buffer = ByteArray(1024)
                        var bytesRead: Int
                        var totalBytesRead: Long = 0

                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                            totalBytesRead += bytesRead
                            val progress: Float = (totalBytesRead.toFloat() / fileSize.toFloat())
                            progressFlow.emit(progress)
                        }
                        inputStream.close()
                        outputStream.close()
                    }
                } catch (e: IOException) {
                    downloadStatusFlow.tryEmit(ERROR)
                    Log.w("Downloader", "Ошибка: $e")
                }
            }
            downloadStatusFlow.emit(END_DOWNLOAD)
        }
        return DownloadState(downloadStatusFlow, progressFlow, fileNameFlow)
    }
}