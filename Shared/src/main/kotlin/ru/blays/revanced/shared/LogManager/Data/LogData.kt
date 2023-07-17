package ru.blays.revanced.shared.LogManager.Data

import androidx.compose.ui.graphics.Color


data class LogData(
    val date: String,
    val type: LogType,
    val tag: String,
    val message: String
)

enum class LogType(val nameOfType: String, val color: Color) {
    DEBUG("Debug", Color.Blue),
    INFO("Info", Color.Green),
    WARN("Warning", Color.Yellow),
    ERROR("Error", Color.Red),;
}