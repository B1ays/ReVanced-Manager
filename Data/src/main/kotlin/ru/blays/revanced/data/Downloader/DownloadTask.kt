package ru.blays.revanced.data.Downloader

import okhttp3.OkHttpClient
import okhttp3.Request
import ru.blays.revanced.data.Downloader.DataClass.DownloadInfo
import ru.blays.revanced.data.Downloader.DataClass.DownloadMode
import ru.blays.revanced.data.Downloader.DataClass.FileMode
import ru.blays.revanced.data.Downloader.DowmloaderImplementation.BaseDownloader
import ru.blays.revanced.data.Downloader.DowmloaderImplementation.InfinityTryDownloader
import ru.blays.revanced.data.Downloader.DowmloaderImplementation.NormalDownloader

private val DEFAULT_DOWNLOAD_MODE = DownloadMode.InfinityTry()
private val DEFAULT_FILE_MODE = FileMode.ContinueIfExists
private const val DEFAULT_FILE_EXTENSION = ".apk"

data class DownloadTask(
    val url: String,
    val fileName: String,
    val fileExtension: String = DEFAULT_FILE_EXTENSION
) {
    var downloadMode: DownloadMode = DEFAULT_DOWNLOAD_MODE
        private set

    var fileMode: FileMode = DEFAULT_FILE_MODE
        private set

    var request: Request = Request.Builder()
        .url(url)
        .build()

    var onSuccess: BaseDownloader.() -> Unit = {}
        private set

    var onError: BaseDownloader.() -> Unit = {}
        private set

    var onPause: BaseDownloader.() -> Unit = {}
        private set

    var onCancel: BaseDownloader.() -> Unit = {}
        private set

    fun setDownloadMode(downloadMode: DownloadMode): DownloadTask = this.apply {
        this.downloadMode = downloadMode
    }

    fun setFileMode(fileMode: FileMode): DownloadTask = this.apply {
        this.fileMode = fileMode
    }

    fun setCustomRequest(request: Request): DownloadTask = this.apply {
        this.request = request
    }

    fun setDefaultActions(
        onSuccess: (BaseDownloader.() -> Unit)? = null,
        onError: (BaseDownloader.() -> Unit)? = null,
        onPause: (BaseDownloader.() -> Unit)? = null,
        onCancel: (BaseDownloader.() -> Unit)? = null
    ): DownloadTask = this.apply {
        onSuccess?.let { this.onSuccess = it }
        onError?.let { this.onError = it }
        onPause?.let { this.onPause = it }
        onCancel?.let { this.onCancel = it }
    }
}

fun DownloadTask.build(okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()): DownloadInfo {
    val client = okHttpClientBuilder.build()
    val downloader: BaseDownloader = when(downloadMode) {
        is DownloadMode.SingleTry -> NormalDownloader(client)
        is DownloadMode.MultipleTry -> NormalDownloader(client)
        is DownloadMode.InfinityTry -> InfinityTryDownloader(client)
    }
    return downloader.download(this)
}