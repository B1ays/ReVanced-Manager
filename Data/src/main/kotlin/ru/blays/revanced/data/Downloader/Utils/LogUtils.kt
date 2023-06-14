package ru.blays.revanced.data.Downloader.Utils

import android.util.Log
import ru.blays.revanced.data.Downloader.Utils.LogType.DEBUG
import ru.blays.revanced.data.Downloader.Utils.LogType.ERROR
import ru.blays.revanced.data.Downloader.Utils.LogType.INFO
import ru.blays.revanced.data.Downloader.Utils.LogType.WARN

private const val LOG_TAG = "Downloader"

internal fun log(message: String, type: LogType = INFO) {
    when (type) {
        DEBUG -> Log.d(LOG_TAG, message)
        INFO -> Log.i(LOG_TAG, message)
        WARN -> Log.w(LOG_TAG, message)
        ERROR -> Log.e(LOG_TAG, message)
    }
}

enum class LogType {
    DEBUG, INFO, WARN, ERROR
}