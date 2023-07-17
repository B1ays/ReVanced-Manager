package ru.blays.revanced.shared.Extensions

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle

fun AnnotatedString.Builder.appendSpacer(spacer: Char = '|') {
    append(" $spacer ")
}

fun AnnotatedString.Builder.appendColored(text: String, color: Color) {
    append(AnnotatedString(
        text,
        SpanStyle(color = color)
    ))
}

fun AnnotatedString.Builder.append(text: String, spanStyle: SpanStyle) {
    append(AnnotatedString(text, spanStyle))
}