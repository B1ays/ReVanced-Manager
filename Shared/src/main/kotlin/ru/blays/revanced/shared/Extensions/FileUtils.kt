package ru.blays.revanced.shared.Extensions

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import java.io.File

private const val FILE_PROVIDER_AUTHORITY = "ru.Blays.ReVanced.Manager.provider"

fun File.open(context: Context) {
    val uri = FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, this)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}

fun DocumentFile.open(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}

fun DocumentFile.getOrCreate(displayName: String, mimeType: String): DocumentFile {
    return if (isDirectory) findFile(displayName)
        ?: createFile(mimeType, displayName)
        ?: throw IllegalStateException("Can't find or create file")
    else throw IllegalStateException("Document file is not a directory")
}

