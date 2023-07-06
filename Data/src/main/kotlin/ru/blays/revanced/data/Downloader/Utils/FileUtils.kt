package ru.blays.revanced.data.Downloader.Utils

import android.os.Environment
import java.io.File
import java.io.RandomAccessFile
import java.nio.channels.FileChannel

private const val DL_FOLDER_NAME = "ReVanced Manager"

private val DEFAULT_FILE_FOLDER: File = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), DL_FOLDER_NAME).apply {
    if (!exists()) mkdir()
}

internal fun File.lengthNotZero(): Boolean = length().isNotZero()

internal fun File.checkFileExists(): Boolean = exists() && lengthNotZero()


internal fun File.createChannel(mode: RWMode): FileChannel {
    val randomAccessFile = RandomAccessFile(this, mode.code)
    return randomAccessFile.channel
}

@Suppress("RedundantLambdaOrAnonymousFunction")
internal var FileChannel.position: Long
    get() = position()
    set(value) = { position(value) }()

@JvmName("createFileExtension")
internal fun String.createFile(fileExtension: String): File {
    return File(DEFAULT_FILE_FOLDER, "${this}${fileExtension}")
}

internal fun createFile(fileName: String, fileExtension: String): File {
    return File(DEFAULT_FILE_FOLDER, "$fileName$fileExtension")
}

internal enum class RWMode(val code: String) {
    READ_ONLY("r"),
    WRITE_ONLY("w"),
    READ_WRITE("rw");
}