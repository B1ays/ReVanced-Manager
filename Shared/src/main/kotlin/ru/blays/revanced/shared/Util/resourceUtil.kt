package ru.blays.revanced.shared.Util

import android.content.Context
import androidx.annotation.StringRes
import org.koin.java.KoinJavaComponent.get

fun getStringRes(@StringRes id: Int, context: Context = get(Context::class.java)): String {
    return try {
        context.getString(id)
    } catch(e: Exception) {
        ""
    }
}