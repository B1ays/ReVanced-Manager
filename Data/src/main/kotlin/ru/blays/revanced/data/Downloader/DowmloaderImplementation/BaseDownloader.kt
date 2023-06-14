package ru.blays.revanced.data.Downloader.DowmloaderImplementation

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

    lateinit var file: File

    abstract val progressFlow: MutableStateFlow<Float>

    abstract val speedFlow: MutableStateFlow<Long>


    fun getContentLength(url: String): Long {
        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).execute().use { response ->
            return response.body.contentLength()
        }
    }
}