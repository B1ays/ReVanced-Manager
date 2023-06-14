package ru.blays.revanced.Elements.DataClasses

import androidx.compose.ui.graphics.Color

data class AccentColorItem(
    val accentDark: Color,
    val accentLight: Color
) {
    companion object {
        var list = listOf(
            AccentColorItem(
                accentDark = Color(0xFFE23939),
                accentLight = Color(0xFF861717)
            ),
            AccentColorItem(
                accentDark = Color(0xFF5D7ED3),
                accentLight = Color(0xFF4B68AF)
            ),
            AccentColorItem(
                accentDark = Color(0xFFE0277E),
                accentLight = Color(0xFF8F0947)
            ),
            AccentColorItem(
                accentDark = Color(0xFF00B4A3),
                accentLight = Color(0xFF05574F)
            ),
            AccentColorItem(
                accentDark = Color(0xFFE76840),
                accentLight = Color(0xFFA22C06)
            ),
            AccentColorItem(
                accentDark = Color(0xFFD6B348),
                accentLight = Color(0xFFC49D28)
            ),
            AccentColorItem(
                accentDark = Color(0xFF724BB8),
                accentLight = Color(0xFF482585)
            ),
            AccentColorItem(
                accentDark = Color(0xFF4DA551),
                accentLight = Color(0xFF226B25)
            )
        )
    }
}