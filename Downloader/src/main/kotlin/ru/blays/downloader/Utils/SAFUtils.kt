package ru.blays.downloader.Utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.documentfile.provider.DocumentFile
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.nio.channels.FileChannel

@SuppressLint("Recycle")
internal fun Context.fileDescriptor(uri: Uri): FileDescriptor? {
    return contentResolver.openFileDescriptor(uri, "rw")?.fileDescriptor
}

internal fun Context.fileDescriptor(documentFile: DocumentFile): FileDescriptor? {
    return contentResolver.openFileDescriptor(documentFile.uri, "rw")?.fileDescriptor
}

internal val FileDescriptor?.channel: FileChannel
    get() {
        val inputStream = FileOutputStream(this)
        return inputStream.channel
    }


internal fun Context.uriPath(uri: Uri): String? {
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = contentResolver.query(
        uri,
        projection,
        null,
        null,
        null
    )
    cursor?.use {
        if (it.moveToFirst()) {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            return it.getString(columnIndex)
        }
    }
    return null
}