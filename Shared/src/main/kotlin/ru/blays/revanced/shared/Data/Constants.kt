package ru.blays.revanced.shared.Data

import android.content.Context
import android.content.Intent
import android.os.Environment
import java.io.File

const val APK_MIME_TYPE = "application/vnd.android.package-archive"
const val APK_FILE_EXTENSION = ".apk"

const val URI_DEFAULT_FLAGS =
    Intent.FLAG_GRANT_READ_URI_PERMISSION or
    Intent.FLAG_GRANT_WRITE_URI_PERMISSION

const val DL_FOLDER_NAME = "ReVanced Manager"
const val INSTALL_CACHE_FOLDER_NAME = "Installer cache"

val DEFAULT_DOWNLOADS_FOLDER: File = File(
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
    DL_FOLDER_NAME
).apply{
    if (!exists()) mkdir()
}

val DEFAULT_INSTALLER_CACHE_FOLDER: (context: Context) -> File = { context ->
    File(context.cacheDir, INSTALL_CACHE_FOLDER_NAME).apply {
        if(!exists()) mkdir()
    }
}