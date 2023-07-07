package ru.blays.revanced.shared.LogManager

import androidx.compose.ui.text.AnnotatedString
import ru.blays.revanced.shared.LogManager.Data.LogData
import java.io.File

interface LogManagerInterface {

    val logList: MutableList<LogData>

    val logDefaultPath: File

    fun getFormattedLog(): AnnotatedString

    fun getRawLog(): String
    fun writeToFile()

    fun d(tag: String, message: String)

    fun w(tag: String, message: String)

    fun i(tag: String, message: String)

    fun e(tag: String, message: String)

}