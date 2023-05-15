package ru.blays.revanced.Presentation.Utils

import android.content.Context
import android.os.Environment
import ru.blays.revanced.data.Utils.FileDownloader
import java.io.File

fun createDownloadSession(fileName: String, url: String, context: Context): FileDownloader {
    val fileDownloader = FileDownloader()
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "$fileName.apk")
    fileDownloader.downloadFile(url, file)
    return fileDownloader
}