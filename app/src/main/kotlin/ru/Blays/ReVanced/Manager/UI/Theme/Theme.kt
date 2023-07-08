package ru.Blays.ReVanced.Manager.UI.Theme

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import ru.blays.revanced.Elements.Util.BuildedTheme


@Suppress("AnimateAsStateLabel")
@SuppressLint("ObsoleteSdkInt")
@Composable
fun ReVancedManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    buildedTheme: BuildedTheme,
    isAmoledTheme: Boolean = false,
    content: @Composable () -> Unit
) {

    val colorScheme = with(
        when {
            dynamicColor && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) && !isAmoledTheme -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            dynamicColor && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) && isAmoledTheme -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context).copy(
                    background = Color.Black,
                    surface = Color.Black,
                    surfaceVariant = Color.Black,
                    surfaceContainer = Color.Black,
                    surfaceTint = Color.Black
                ) else dynamicLightColorScheme(context)
            }
            darkTheme && isAmoledTheme -> buildedTheme.darkColorScheme.copy(
                background = Color.Black,
                surface = Color.Black,
                surfaceVariant = Color.Black,
                surfaceContainer = Color.Black,
                surfaceTint = Color.Black
            )
            darkTheme -> buildedTheme.darkColorScheme
            else -> {
                buildedTheme.lightColorScheme
            }
        }
    ) {
        val primary by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.primary)
        val onPrimary by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.onPrimary)
        val primaryContainer by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.primaryContainer)
        val onPrimaryContainer by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.onPrimaryContainer)
        val inversePrimary by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.inversePrimary)
        val secondary by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.secondary)
        val onSecondary by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.onSecondary)
        val secondaryContainer by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.secondaryContainer)
        val onSecondaryContainer by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.onSecondaryContainer)
        val tertiary by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.tertiary)
        val onTertiary by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.onTertiary)
        val tertiaryContainer by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.tertiaryContainer)
        val onTertiaryContainer by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.onTertiaryContainer)
        val background by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.background)
        val onBackground by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.onBackground)
        val surface by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.surface)
        val onSurface by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.onSurface)
        val surfaceVariant by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.surfaceVariant)
        val onSurfaceVariant by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.onSurfaceVariant)
        val surfaceTint by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.surfaceTint)
        val inverseSurface by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.inverseSurface)
        val inverseOnSurface by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.inverseOnSurface)
        val error by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.error)
        val onError by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.onError)
        val errorContainer by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.errorContainer)
        val onErrorContainer by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.onErrorContainer)
        val outline by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.outline)
        val outlineVariant by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.outlineVariant)
        val scrim by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = this.scrim)

        ColorScheme(
            primary,
            onPrimary,
            primaryContainer,
            onPrimaryContainer,
            inversePrimary,
            secondary,
            onSecondary,
            secondaryContainer,
            onSecondaryContainer,
            tertiary,
            onTertiary,
            tertiaryContainer,
            onTertiaryContainer,
            background,
            onBackground,
            surface,
            onSurface,
            surfaceVariant,
            onSurfaceVariant,
            surfaceTint,
            inverseSurface,
            inverseOnSurface,
            error,
            onError,
            errorContainer,
            onErrorContainer,
            outline,
            outlineVariant,
            scrim
        )
    }


    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@SuppressLint("ObsoleteSdkInt")
@Composable
fun ReVancedManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {

    val colorScheme = when {
        dynamicColor && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }

    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}