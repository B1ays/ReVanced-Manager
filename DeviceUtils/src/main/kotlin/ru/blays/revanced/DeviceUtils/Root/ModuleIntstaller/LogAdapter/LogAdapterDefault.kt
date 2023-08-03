package ru.blays.revanced.DeviceUtils.Root.ModuleIntstaller.LogAdapter

import android.util.Log

class LogAdapterDefault: LogAdapter {

    override fun d(message: String) {
        Log.d(TAG, message)
    }

    override fun i(message: String) {
        Log.i(TAG, message)
    }

    override fun w(message: String) {
        Log.w(TAG, message)
    }

    override fun e(message: String) {
        Log.e(TAG, message)
    }
}