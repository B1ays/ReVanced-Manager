package ru.Blays.ReVanced.Manager.Utils.DownloaderLogAdapter

import ru.blays.revanced.data.Downloader.LogAdapter.LogAdapterAbstract
import ru.blays.revanced.shared.LogManager.BLog

class LogAdapterBLog: LogAdapterAbstract() {
    override fun d(message: String) {
        BLog.d(TAG, message)
    }

    override fun i(message: String) {
        BLog.i(TAG, message)
    }

    override fun w(message: String) {
        BLog.w(TAG, message)
    }

    override fun e(message: String) {
        BLog.e(TAG, message)
    }

}