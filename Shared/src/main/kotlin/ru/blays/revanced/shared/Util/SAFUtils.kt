package ru.blays.revanced.shared.Util

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import androidx.documentfile.provider.DocumentFile
import java.io.FileDescriptor
import java.io.FileInputStream
import java.nio.channels.FileChannel

@SuppressLint("Recycle")
fun Context.fileDescriptor(uri: Uri): ParcelFileDescriptor? {
    return contentResolver.openFileDescriptor(uri, "rw")
}

fun Context.fileDescriptor(documentFile: DocumentFile): ParcelFileDescriptor? {
    return contentResolver.openFileDescriptor(documentFile.uri, "rw")
}

val FileDescriptor?.channel: FileChannel
    get() {
        val inputStream = FileInputStream(this)
        return inputStream.channel
    }


fun Context.uriPath(uri: Uri): String? {
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