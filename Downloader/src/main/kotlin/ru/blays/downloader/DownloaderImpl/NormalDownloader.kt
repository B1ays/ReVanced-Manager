package ru.blays.downloader.DownloaderImpl

import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.yield
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.IOException
import org.koitharu.pausingcoroutinedispatcher.launchPausing
import ru.blays.downloader.DataClass.DownloadInfo
import ru.blays.downloader.DataClass.FileMode
import ru.blays.downloader.DataClass.LogType
import ru.blays.downloader.DataClass.StorageMode
import ru.blays.downloader.DownloadTask
import ru.blays.downloader.Utils.RWMode
import ru.blays.downloader.Utils.createChannel
import ru.blays.downloader.Utils.createFile
import ru.blays.downloader.Utils.createResponse
import ru.blays.downloader.Utils.isNull
import ru.blays.downloader.Utils.position
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

@Suppress("KotlinConstantConditions")
internal class NormalDownloader(httpClient: OkHttpClient): BaseDownloader() {

    override val client = httpClient

    override val progressFlow = MutableStateFlow(0F)

    override val speedFlow = MutableStateFlow(0L)

    override fun download(task: DownloadTask): DownloadInfo? {

        val log = task.logAdapter::log

        val file = createFile(fileName = task.fileName, fileExtension = task.fileExtension)

        if (file == null) {
            log("Unable to create file", LogType.ERROR)
            task.onError(this)
            return null
        }

        val fileLength: Long

        val channel: FileChannel

        if (task.storageMode == StorageMode.FileIO) {
            if (task.fileMode == FileMode.Recreate) file.delete()

            // Create file from name and extension
            val file = createFile(
                fileName = task.fileName,
                fileExtension = task.fileExtension
            )

            if (file == null) {
                log("Unable to create file", LogType.ERROR)
                task.onError(this)
                return null
            }

            // write file to class property
            this.file = file

            fileLength = file.length()
            channel = file.createChannel(mode = RWMode.READ_WRITE)
        } else {
            if (task.fileMode == FileMode.Recreate) task.simpleDocument?.delete()
            val outputStream = task.simpleDocument?.outputStream ?: return null
            channel = outputStream.channel
            fileLength = task.simpleDocument?.length ?: 0L
        }

        var isRunning = true



        val job = launchPausing mainJob@ {

            try {

                val response: Response


                val originalFileSize = getContentLength(task.url).also {
                    if (it.isNull()) return@mainJob
                }

                if (fileLength == originalFileSize) {
                    task.onSuccess(this@NormalDownloader)
                    return@mainJob
                }

                response = if (task.fileMode == FileMode.ContinueIfExists && fileLength < originalFileSize!!) {
                    channel.position = fileLength
                    val newRequest = task.request.newBuilder()
                        .addHeader("Range", "bytes=${fileLength}-")
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

                        val byteBuffer = ByteBuffer.wrap(buffer, 0, bytesRead)

                        channel.write(byteBuffer)

                        totalBytesRead += bytesRead
                        val progress: Float = ((totalBytesRead + fileLength).toFloat() / originalFileSize!!.toFloat())
                        progressFlow.emit(progress)
                    }

                    channel.close()
                    inputStream.close()
                    downloadSpeedJob.cancel()
                    resp.close()
                    task.onSuccess(this@NormalDownloader)
                    log("Download complete", LogType.INFO)
                }
            } catch (e: IOException) {
                log("Response error, exception: $e", LogType.ERROR)
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
            task.simpleDocument,
            progressFlow,
            speedFlow,
            actionPauseResume,
            actionCancel
        )
    }
}