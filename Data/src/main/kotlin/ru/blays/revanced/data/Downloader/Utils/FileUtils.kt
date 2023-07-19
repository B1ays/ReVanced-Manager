package ru.blays.revanced.data.Downloader.Utils

import android.os.Environment
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.channels.FileChannel

private const val DL_FOLDER_NAME = "ReVanced Manager"

val DEFAULT_DOWNLOADS_FOLDER: File = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), DL_FOLDER_NAME).also {
    if (!it.exists()) it.mkdir()
}

internal fun File.lengthNotZero(): Boolean = length().isNotZero()

internal fun File.checkFileExists(): Boolean = exists() && lengthNotZero()

internal fun File.createChannel(mode: RWMode): FileChannel = try {
    val randomAccessFile = RandomAccessFile(this, mode.code)
    randomAccessFile.channel
} catch (_: IOException) {
    createChannel(mode)
}


@JvmName("setPositionInternal")
private fun FileChannel.setPosition(position: Long) {
    position(position)
}

internal var FileChannel.position: Long
    get() = position()
    set(value) = setPosition(value)

@JvmName("createFileExtension")
internal fun String.createFile(fileExtension: String): File {
    return File(DEFAULT_DOWNLOADS_FOLDER, "${this}${fileExtension}")
}

internal fun createFile(fileName: String, fileExtension: String): File? {
    val file = File(DEFAULT_DOWNLOADS_FOLDER, "$fileName$fileExtension")
    if (!file.exists()) {
        if (!file.createNewFile()) return null
    }
    return file
}

internal enum class RWMode(val code: String) {
    READ_ONLY("r"),
    WRITE_ONLY("w"),
    READ_WRITE("rw");
}