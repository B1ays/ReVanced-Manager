package ru.blays.revanced.shared.Extensions

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

private const val FILE_PROVIDER_AUTHORITY = "ru.Blays.ReVanced.Manager.provider"

fun File.open(context: Context) {
    val uri = FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, this)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}