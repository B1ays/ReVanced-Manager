package ru.blays.revanced.Presentation.Utils

import android.content.Context
import android.os.Environment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import ru.blays.revanced.Services.PublicApi.PackageManagerApi
import ru.blays.revanced.data.Utils.FileDownloader
import java.io.File

internal fun createDownloadAndInstallSession(
    fileName: String,
    url: String,
    context: Context,
    installerType: Int
): FileDownloader {
    val fileDownloader = FileDownloader()
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "$fileName.apk")

    fileDownloader.downloadFile(url, file)
    CoroutineScope(Dispatchers.Default).launch {
        fileDownloader.downloadStatusFlow.collect {
            if (it == FileDownloader.END_DOWNLOAD) {
                val packageManager: PackageManagerApi by inject(PackageManagerApi::class.java)
                packageManager.installApk(file, installerType)
            }
        }
    }

    return fileDownloader
}