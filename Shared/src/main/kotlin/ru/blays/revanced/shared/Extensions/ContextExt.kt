package ru.blays.revanced.shared.Extensions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.core.content.getSystemService
import java.io.File

fun Context.share(text: String?) {
    try {
        ShareCompat.IntentBuilder(this)
            .setType("text/plain")
            .setChooserTitle("Share")
            .setText(text)
            .startChooser()
    } catch (_: Exception) {}
}

fun Context.copyToClipBoard(data: String?) {
    val clipboard = getSystemService<ClipboardManager>()
    val clip = ClipData.newPlainText("Text", data)
    clipboard?.setPrimaryClip(clip)
}

inline fun <reified T> Context.intentFor(): Intent {
    return Intent(this, T::class.java)
}

fun Context.writeTextByUri(uri: Uri, text: String) {
    val bytesInputStream = text.byteInputStream()
    val buffer = ByteArray(1024)

    val outputStream = contentResolver.openOutputStream(uri)
    while (bytesInputStream.read(buffer) != -1) {
        outputStream?.write(buffer)
    }
    outputStream?.close()
}

fun Context.findActivity(): ComponentActivity {
    var context = this
    while (context is ContextWrapper) {
        if (context is ComponentActivity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Permissions should be called in the context of an Activity")
}

fun Context.getFileUri(file: File): Uri {
    return FileProvider.getUriForFile(
        this,
        this.packageName+".provider",
        file
    )
}