package ru.blays.revanced.data.Downloader.DowmloaderImplementation

import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.yield
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.IOException
import org.koitharu.pausingcoroutinedispatcher.launchPausing
import ru.blays.revanced.data.Downloader.DataClass.DownloadInfo
import ru.blays.revanced.data.Downloader.DataClass.FileMode
import ru.blays.revanced.data.Downloader.DownloadTask
import ru.blays.revanced.data.Downloader.Utils.LogType
import ru.blays.revanced.data.Downloader.Utils.RWMode
import ru.blays.revanced.data.Downloader.Utils.checkFileExists
import ru.blays.revanced.data.Downloader.Utils.createChannel
import ru.blays.revanced.data.Downloader.Utils.createFile
import ru.blays.revanced.data.Downloader.Utils.createResponse
import ru.blays.revanced.data.Downloader.Utils.isNull
import ru.blays.revanced.data.Downloader.Utils.log
import java.nio.ByteBuffer

@Suppress("KotlinConstantConditions")
class NormalDownloader(httpClient: OkHttpClient): BaseDownloader() {

    override val client = httpClient

    override val progressFlow = MutableStateFlow(0F)

    override val speedFlow = MutableStateFlow(0L)

    override fun download(task: DownloadTask): DownloadInfo {

        val file = createFile(fileName = task.fileName, fileExtension = task.fileExtension)

        this.file = file

        val channel = file.createChannel(mode = RWMode.READ_WRITE)

        var isRunning = true

        if (task.fileMode == FileMode.Recreate) file.delete()

        val job = launchPausing mainJob@ {

            try {

                val response: Response

                val fileSize = file.length()

                val originalFileSize = getContentLength(task.url).also {
                    if (it.isNull()) return@mainJob
                }

                if (file.checkFileExists() && fileSize == originalFileSize) {
                    task.onSuccess(this@NormalDownloader)
                    return@mainJob
                }

                response = if (task.fileMode == FileMode.ContinueIfExists && fileSize < originalFileSize!!) {
                    channel.position(fileSize)
                    val newRequest = task.request.newBuilder()
                        .addHeader("Range", "bytes=${fileSize}-")
                        .build()
                    client.createResponse(newRequest)
                } else {
                    client.createResponse(task.request)
                }

                response.use { resp ->
                    if (!resp.isSuccessful) {
                        log("Response not successful, errorCode: ${resp.code}", LogType.WARN)
                        task.onError(this@NormalDownloader)
                        this.cancel()
                        return@mainJob
                    }

                    val inputStream = response.body.byteStream()

                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    var totalBytesRead: Long = 0

                    downloadSpeedJob.launchPausing speedCalculateJob@  {
                        while (this@mainJob.isActive) {
                            yield()
                            val startValue = totalBytesRead // total bytes read value
                            delay(500) // wait 0,5 second
                            val endValue = totalBytesRead // new bytes value
                            val speed = ((endValue - startValue) / 1024) * 2 // speed in kilobytes per seconds
                            speedFlow.emit(speed) // send value to flow
                        }
                    }

                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {

                        yield()

                        log("Read from stream")

                        val byteBuffer = ByteBuffer.wrap(buffer, 0, bytesRead)

                        channel.write(byteBuffer)

                        totalBytesRead += bytesRead
                        val progress: Float = ((totalBytesRead + fileSize).toFloat() / originalFileSize!!.toFloat())
                        progressFlow.emit(progress)
                    }

                    channel.close()
                    inputStream.close()
                    downloadSpeedJob.cancel()
                    resp.close()
                    task.onSuccess(this@NormalDownloader)
                }
            } catch (e: IOException) {
                log("Response error, exception: $e")
                task.onError(this@NormalDownloader)
                downloadSpeedJob.cancel()
                this.cancel()
                return@mainJob
            }
        }

        val actionPauseResume = {
            log("Pause or resume download", LogType.DEBUG)
            if (isRunning) {
                isRunning = false
                job.pause()
            } else {
                isRunning = true
                job.resume()
            }
        }

        val actionCancel = {
            task.onCancel(this@NormalDownloader)
            job.cancel(cause = null)
        }

        return DownloadInfo(
            task.fileName,
            file,
            progressFlow,
            speedFlow,
            actionPauseResume,
            actionCancel
        )
    }
}