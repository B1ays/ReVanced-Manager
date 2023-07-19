package ru.blays.revanced.data.Downloader.DownloaderImpl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.blays.revanced.data.Downloader.Downloader
import java.io.File

abstract class BaseDownloader: Downloader, CoroutineScope {

    override val coroutineContext = Dispatchers.IO

    val downloadSpeedJob = CoroutineScope(Dispatchers.Default)

    abstract val client: OkHttpClient

    var file: File? = null

    abstract val progressFlow: MutableStateFlow<Float>

    abstract val speedFlow: MutableStateFlow<Long>

    fun getContentLength(url: String): Long? {
        try {
            val request = Request.Builder()
                .url(url)
                .build()
            client.newCall(request).execute().use { response ->
                return response.body.contentLength()
            }
        } catch (_: Exception) {
            return null
        }

    }
}