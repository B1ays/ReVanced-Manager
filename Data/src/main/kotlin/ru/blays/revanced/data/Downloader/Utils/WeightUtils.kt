package ru.blays.revanced.data.Downloader.Utils

import java.math.RoundingMode

fun Long.isNotZero(): Boolean = this != 0L

fun Long.formatSize(): String {
    require(this >= 0) { "Size must larger than 0." }

    val byte = this.toDouble()
    val kb = byte / 1024.0
    val mb = byte / 1024.0 / 1024.0
    val gb = byte / 1024.0 / 1024.0 / 1024.0
    val tb = byte / 1024.0 / 1024.0 / 1024.0 / 1024.0

    return when {
        tb >= 1 -> "${tb.decimal(2)} TB"
        gb >= 1 -> "${gb.decimal(2)} GB"
        mb >= 1 -> "${mb.decimal(2)} MB"
        kb >= 1 -> "${kb.decimal(2)} KB"
        else -> "${byte.decimal(2)} B"
    }
}

fun Double.decimal(digits: Int): Double {
    return toBigDecimal()
        .setScale(digits, RoundingMode.HALF_UP)
        .toDouble()
}