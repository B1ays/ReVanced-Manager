package ru.blays.revanced.shared.Util

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.coroutineScope
import java.io.File

suspend fun Context.copyToTemp(documentFile: DocumentFile, file: File): File? = coroutineScope {
    val outputStream = file.outputStream()
    val inputStream = contentResolver.openInputStream(documentFile.uri) ?: return@coroutineScope null
    inputStream.copyTo(outputStream)
    inputStream.close()
    outputStream.close()
    return@coroutineScope file
}