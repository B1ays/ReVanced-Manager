package ru.blays.revanced.Elements.DataClasses

import androidx.compose.ui.graphics.Color

data class AccentColorItem(
    val title: String,
    val accentDark: Color,
    val accentLight: Color
) {
    companion object {
        var list = listOf(
            AccentColorItem(
                title = "Красный",
                accentDark = Color(0xFFE23939),
                accentLight = Color(0xFF861717)
            ),
            AccentColorItem(
                title = "Синий",
                accentDark = Color(0xFF5D7ED3),
                accentLight = Color(0xFF4B68AF)
            ),
            AccentColorItem(
                title = "Розовый",
                accentDark = Color(0xFFE0277E),
                accentLight = Color(0xFF8F0947)
            ),
            AccentColorItem(
                title = "Бирюзовый",
                accentDark = Color(0xFF00B4A3),
                accentLight = Color(0xFF05574F)
            ),
            AccentColorItem(
                title = "Ораньжевый",
                accentDark = Color(0xFFE76840),
                accentLight = Color(0xFFA22C06)
            ),
            AccentColorItem(
                title = "Жёлтый",
                accentDark = Color(0xFFD6B348),
                accentLight = Color(0xFFC49D28)
            ),
            AccentColorItem(
                title = "Фиолетовый",
                accentDark = Color(0xFF724BB8),
                accentLight = Color(0xFF482585)
            ),
            AccentColorItem(
                title = "Зелёный",
                accentDark = Color(0xFF4DA551),
                accentLight = Color(0xFF226B25)
            )
        )
    }
}