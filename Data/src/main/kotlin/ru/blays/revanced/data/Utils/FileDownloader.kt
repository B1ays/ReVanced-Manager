package ru.blays.revanced.data.Utils

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import ru.blays.revanced.data.Utils.FileDownloader.Status.END_DOWNLOAD
import ru.blays.revanced.data.Utils.FileDownloader.Status.ERROR
import ru.blays.revanced.data.Utils.FileDownloader.Status.START_DOWNLOAD
import ru.blays.revanced.data.Utils.FileDownloader.Status.START_REQUEST
import java.io.File
import java.io.FileOutputStream

interface FileDownloaderInterface {

    val downloadStatusFlow: MutableStateFlow<String>

    val progressFlow: MutableStateFlow<Float>

    suspend fun downloadFile(url: String, file: File): Job

}

object FileDownloader : FileDownloaderInterface {

    object Status {
        const val START_REQUEST = "START_REQUEST"
        const val START_DOWNLOAD = "START_DOWNLOAD"
        const val END_DOWNLOAD = "END_DOWNLOAD"
        const val ERROR = "ERROR"
    }

    override val downloadStatusFlow = MutableStateFlow(START_REQUEST)

    override val progressFlow = MutableStateFlow(0F)

    override suspend fun downloadFile(url: String, file: File) = CoroutineScope(Dispatchers.IO).launch {

        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url = url)
            .build()

        try {

            downloadStatusFlow.tryEmit(START_REQUEST)

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException(
                        "Запрос к серверу не был успешен:" +
                                " ${response.code} ${response.message}"
                    )
                }

                if (file.exists()) {
                    downloadStatusFlow.tryEmit(END_DOWNLOAD)
                    Log.i("Downloader", "file already exists")
                    this.cancel()
                    return@launch
                }

                Log.i("Downloader", "download update")

                downloadStatusFlow.tryEmit(START_DOWNLOAD)

                val inputStream = response.body.byteStream()
                val outputStream = FileOutputStream(file)


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
        }
    }
}