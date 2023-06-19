package ru.blays.revanced.shared.Extensions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.core.app.ShareCompat
import androidx.core.content.getSystemService

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