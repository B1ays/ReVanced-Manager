package ru.blays.revanced.shared.LogManager

import android.os.Environment
import android.util.Log
import androidx.compose.ui.text.AnnotatedString
import ru.blays.revanced.shared.Extensions.appendColored
import ru.blays.revanced.shared.Extensions.appendSpacer
import ru.blays.revanced.shared.Extensions.defaultFormatter
import ru.blays.revanced.shared.Extensions.getCurrentFormattedTime
import ru.blays.revanced.shared.LogManager.Data.LogData
import ru.blays.revanced.shared.LogManager.Data.LogType
import java.io.File

object BLog: LogManagerInterface {
    override val logList: MutableList<LogData> = mutableListOf()

    override val logDefaultPath: File = Environment.getDataDirectory()

    override fun getFormattedLog(): AnnotatedString {
        val builder = AnnotatedString.Builder()
        logList.forEach { data ->
            with(builder) {
                append(data.date)
                appendSpacer()
                appendColored(data.type.nameOfType, data.type.color)
                appendSpacer()
                append(data.tag)
                appendSpacer('-')
                append(data.message)
                appendLine()
            }
        }
        return builder.toAnnotatedString()
    }

    override fun getRawLog(): String {
        val stringBuilder = StringBuilder()
        logList.forEach { data ->
            stringBuilder
                .append(data.date)
                .append(" | ")
                .append(data.type.nameOfType)
                .append(" | ")
                .append(data.tag)
                .append(" | ")
                .append(data.message)
                .append("\n")
        }
        return stringBuilder.toString()
    }

    private fun log(logData: LogData) {
        logList.add(logData)
    }

    override fun d(tag: String, message: String) {
        Log.d(tag, message)
        log(
            LogData(
                date = getCurrentFormattedTime(defaultFormatter),
                type = LogType.DEBUG,
                tag = tag,
                message = message
            )
        )
    }

    override fun w(tag: String, message: String) {
        Log.w(tag, message)
        log(
            LogData(
                date = getCurrentFormattedTime(defaultFormatter),
                type = LogType.WARN,
                tag = tag,
                message = message
            )
        )
    }

    override fun i(tag: String, message: String) {
        Log.i(tag, message)
        log(
            LogData(
                date = getCurrentFormattedTime(defaultFormatter),
                type = LogType.INFO,
                tag = tag,
                message = message
            )
        )
    }

    override fun e(tag: String, message: String) {
        Log.e(tag, message)
        log(
            LogData(
                date = getCurrentFormattedTime(defaultFormatter),
                type = LogType.ERROR,
                tag = tag,
                message = message
            )
        )
    }
}