package ru.blays.downloader

import okhttp3.OkHttpClient
import okhttp3.Request
import ru.blays.downloader.DataClass.DownloadInfo
import ru.blays.downloader.DataClass.DownloadMode
import ru.blays.downloader.DataClass.FileMode
import ru.blays.downloader.DataClass.StorageMode
import ru.blays.downloader.DownloaderImpl.BaseDownloader
import ru.blays.downloader.DownloaderImpl.InfinityTryDownloader
import ru.blays.downloader.DownloaderImpl.NormalDownloader
import ru.blays.downloader.LogAdapter.LogAdapterAbstract
import ru.blays.downloader.LogAdapter.LogAdapterDefault
import ru.blays.simpledocument.SimpleDocument

private val DEFAULT_DOWNLOAD_MODE = DownloadMode.InfinityTry
private val DEFAULT_FILE_MODE = FileMode.ContinueIfExists
private const val DEFAULT_FILE_EXTENSION = ".apk"

class DownloadTask {

    var url: String = ""

    var fileName: String = ""

    val fileExtension: String = DEFAULT_FILE_EXTENSION

    var storageMode: StorageMode = StorageMode.FileIO

    var simpleDocument: SimpleDocument? = null

    var downloadMode: DownloadMode = DEFAULT_DOWNLOAD_MODE
        private set

    var fileMode: FileMode = DEFAULT_FILE_MODE
        private set

    val request: Request get() = Request
        .Builder()
        .url(url)
        .build()

    var logAdapter: LogAdapterAbstract = LogAdapterDefault()

    var onSuccess: BaseDownloader.() -> Unit = {}
        private set

    var onError: BaseDownloader.() -> Unit = {}
        private set

    var onPause: BaseDownloader.() -> Unit = {}
        private set

    var onCancel: BaseDownloader.() -> Unit = {}
        private set

    fun onSuccess(block: BaseDownloader.() -> Unit) {
        onSuccess = block
    }

    fun onError(block: BaseDownloader.() -> Unit) {
        onError = block
    }

    fun onPause(block: BaseDownloader.() -> Unit) {
        onPause = block
    }

    fun onCancel(block: BaseDownloader.() -> Unit) {
        onCancel = block
    }

    companion object {
        fun builder(scope: DownloadTask.() -> Unit): DownloadTask {
            val downloadTask = DownloadTask()
            scope(downloadTask)
            require(
                when {
                    downloadTask.storageMode == StorageMode.FileIO &&
                            downloadTask.fileName.isNotEmpty() -> true
                    downloadTask.storageMode == StorageMode.SAF &&
                            downloadTask.simpleDocument != null -> true
                    else -> false
                }
            )  { "Unable to create download task with this storage parameters" }
            require(downloadTask.url.isNotEmpty()) { "No value passed for [url]" }
            return downloadTask
        }
    }
}

fun DownloadTask.build(okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()): DownloadInfo? {
    val client = okHttpClientBuilder.build()
    val downloader: BaseDownloader = when(downloadMode) {
        is DownloadMode.SingleTry -> NormalDownloader(client)
        is DownloadMode.MultipleTry -> NormalDownloader(client)
        is DownloadMode.InfinityTry -> InfinityTryDownloader(client)
    }
    return downloader.download(this)
}