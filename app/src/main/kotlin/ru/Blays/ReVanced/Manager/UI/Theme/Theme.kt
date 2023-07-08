package ru.Blays.ReVanced.Manager.UI.Theme

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.isSystemInDarkTheme
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

    val colorScheme = when {
        dynamicColor && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) -> {
            val context = LocalContext.current
            val darkColorScheme = dynamicDarkColorScheme(context)
            val lightColorScheme = dynamicLightColorScheme(context)
            val background by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = if (isAmoledTheme) Color.Black else darkColorScheme.background)
            val surface by animateColorAsState(animationSpec = spring(stiffness = 300F),targetValue = if (isAmoledTheme) Color.Black else darkColorScheme.surface)
            val surfaceVariant by animateColorAsState(animationSpec = spring(stiffness = 300F),targetValue = if (isAmoledTheme) Color.Black else darkColorScheme.surfaceVariant)
            val surfaceContainer by animateColorAsState(animationSpec = spring(stiffness = 300F),targetValue = if (isAmoledTheme) Color.Black else darkColorScheme.surfaceContainer)
            val surfaceTint by animateColorAsState(animationSpec = spring(stiffness = 300F),targetValue = if (isAmoledTheme) Color.Black else darkColorScheme.surfaceTint)
            val newDarkColorScheme = darkColorScheme.copy(
                background = background,
                surface = surface,
                surfaceVariant = surfaceVariant,
                surfaceContainer = surfaceContainer,
                surfaceTint = surfaceTint
            )
            if (darkTheme) newDarkColorScheme else lightColorScheme
        }
        darkTheme -> {
            val background by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = if (isAmoledTheme) Color.Black else buildedTheme.darkColorScheme.background)
            val surface by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = if (isAmoledTheme) Color.Black else buildedTheme.darkColorScheme.surface)
            val surfaceVariant by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = if (isAmoledTheme) Color.Black else buildedTheme.darkColorScheme.surfaceVariant)
            val surfaceContainer by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = if (isAmoledTheme) Color.Black else buildedTheme.darkColorScheme.surfaceContainer)
            val surfaceTint by animateColorAsState(animationSpec = spring(stiffness = 300F), targetValue = if (isAmoledTheme) Color.Black else buildedTheme.darkColorScheme.surfaceTint)
            buildedTheme.darkColorScheme.copy(
                background = background,
                surface = surface,
                surfaceVariant = surfaceVariant,
                surfaceContainer = surfaceContainer,
                surfaceTint = surfaceTint
            )
        }
        else -> buildedTheme.lightColorScheme
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