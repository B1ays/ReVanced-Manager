package ru.blays.revanced.Elements.Elements.CustomRippleTheme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

class CustomRippleTheme(private val rippleColor: Color) : RippleTheme {

    @Composable
    override fun defaultColor() =
        RippleTheme.defaultRippleColor(
            rippleColor,
            lightTheme = !isSystemInDarkTheme()
        )

    @Composable
    override fun rippleAlpha(): RippleAlpha =
        RippleTheme.defaultRippleAlpha(
            rippleColor.copy(alpha = 0.75f),
            lightTheme = !isSystemInDarkTheme()
        )
}