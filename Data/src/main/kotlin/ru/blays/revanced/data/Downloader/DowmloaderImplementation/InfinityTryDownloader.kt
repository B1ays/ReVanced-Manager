package ru.blays.revanced.data.Downloader.DowmloaderImplementation

import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.yield
import okhttp3.OkHttpClient
import okio.IOException
import org.koitharu.pausingcoroutinedispatcher.launchPausing
import ru.blays.revanced.data.Downloader.DataClass.DownloadInfo
import ru.blays.revanced.data.Downloader.DataClass.LogType
import ru.blays.revanced.data.Downloader.DownloadTask
import ru.blays.revanced.data.Downloader.Utils.RWMode
import ru.blays.revanced.data.Downloader.Utils.createChannel
import ru.blays.revanced.data.Downloader.Utils.createFile
import ru.blays.revanced.data.Downloader.Utils.createResponse
import ru.blays.revanced.data.Downloader.Utils.isNull
import ru.blays.revanced.data.Downloader.Utils.position
import java.nio.ByteBuffer
import kotlin.time.Duration.Companion.seconds

@Suppress("KotlinConstantConditions")
class InfinityTryDownloader(httpClient: OkHttpClient): BaseDownloader() {

    override val client = httpClient

    // Flow with download progress & speed
    override val progressFlow = MutableStateFlow(0F)
    override val speedFlow = MutableStateFlow(0L)

    override fun download(task: DownloadTask): DownloadInfo {

        val log = task.logAdapter::log

        // Create file from name and extension
        val file = createFile(fileName = task.fileName, fileExtension = task.fileExtension)

        // write file to lateinit variable
        this.file = file

        // create fileChannel using RandomAccessFile
        val channel = file.createChannel(mode = RWMode.READ_WRITE)

        // Download process running status
        var isRunning = true

        val job = launchPausing mainJob@ {

            var totalBytesRead = 0L

            val fileSize = file.length()

            log("file size: $fileSize bytes", LogType.INFO)

            val originalFileSize = getContentLength(task.url).also {
                if (it.isNull()) return@mainJob
            }

            /*
            Check file exists.
            if exist && file size = real size -> download complete
            if file not exist or file size != real size -> start download & append file size to totalBytesRead
            */
            if (/*file.checkFileExists() && */fileSize == originalFileSize) {
                task.onSuccess(this@InfinityTryDownloader)
                return@mainJob
            } else {
                // Add existing file size to totalBytesRead
                totalBytesRead += fileSize
                // Set channel position to totalBytesRead
                channel.position = totalBytesRead
            }

            while(totalBytesRead < originalFileSize!!) {

                try {

                    log("Create new request", LogType.INFO)

                    log("total bytes read: $totalBytesRead, channel position: ${channel.position}", LogType.DEBUG)

                    // Create new request & add header with bytes range
                    val newRequest = task.request.newBuilder()
                        .addHeader("Range", "bytes=${totalBytesRead}-")
                        .build()

                    // Create response
                    val response = client.createResponse(newRequest)

                    response.use { resp ->

                        if (!resp.isSuccessful) {
                            log("Response not successful, errorCode: ${resp.code}", LogType.WARN)
                            delay(2.seconds)
                            return@use
                        }

                        val inputStream = response.body.byteStream()

                        val buffer = ByteArray(1024)
                        var bytesRead: Int

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

                            val byteBuffer = ByteBuffer.wrap(buffer, 0, bytesRead)

                            channel.write(byteBuffer)

                            totalBytesRead += bytesRead
                            val progress: Float = (totalBytesRead.toFloat() / originalFileSize.toFloat())
                            progressFlow.emit(progress)
                        }

                        // Close channels & streams
                        task.onSuccess(this@InfinityTryDownloader)
                        channel.close()
                        inputStream.close()
                        downloadSpeedJob.cancel()
                        resp.close()
                        log("Download complete", LogType.INFO)
                    }
                } catch (e: IOException) {
                    log("Response error, exception: $e", LogType.WARN)
                    downloadSpeedJob.cancel()
                    delay(2.seconds)
                }
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
            task.onCancel(this@InfinityTryDownloader)
            job.cancel()
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