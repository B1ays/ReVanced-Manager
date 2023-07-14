package ru.blays.revanced.data.Downloader.LogAdapter

import ru.blays.revanced.data.Downloader.DataClass.LogType

@Suppress("PropertyName")
abstract class LogAdapterAbstract {

    val TAG: String = "Downloader"

    internal fun log(message: String, type: LogType) {
        when (type) {
            LogType.DEBUG -> d(message)
            LogType.INFO -> i(message)
            LogType.WARN -> w(message)
            LogType.ERROR -> e(message)
        }
    }

     abstract fun d(message: String)

    abstract fun i(message: String)

    abstract fun w(message: String)

    abstract fun e(message: String)

}