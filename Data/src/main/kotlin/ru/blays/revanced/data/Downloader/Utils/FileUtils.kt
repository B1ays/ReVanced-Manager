package ru.blays.revanced.data.Downloader.Utils

import android.os.Environment
import java.io.File
import java.io.RandomAccessFile
import java.nio.channels.FileChannel

private val DEFAULT_FILE_FOLDER: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

fun File.lengthNotZero(): Boolean = length().isNotZero()

fun File.checkFileExists(): Boolean = exists() && lengthNotZero()


fun File.createChannel(mode: RWMode): FileChannel {
    val randomAccessFile = RandomAccessFile(this, mode.code)
    return randomAccessFile.channel
}

@JvmName("createFileExtension")
fun String.createFile(fileExtension: String): File {
    return File(DEFAULT_FILE_FOLDER, "${this}${fileExtension}")
}

fun createFile(fileName: String, fileExtension: String): File {
    return File(DEFAULT_FILE_FOLDER, "$fileName$fileExtension")
}

enum class RWMode(val code: String) {
    READ_ONLY("r"),
    WRITE_ONLY("w"),
    READ_WRITE("rw");
}