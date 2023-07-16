package ru.blays.helios.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember

@Composable
fun Screen.LifecycleEffect(
    onStarted: () -> Unit = {},
    onDisposed: () -> Unit = {}
) {
    DisposableEffect(key) {
        onStarted()
        onDispose(onDisposed)
    }
}

@Composable
fun rememberScreenLifecycleOwner(
    screen: Screen
): ScreenLifecycleOwner =
    remember(screen.key) {
        when (screen) {
            is ScreenLifecycleProvider -> screen.getLifecycleOwner()
            else -> DefaultScreenLifecycleOwner
        }
    }

@Composable
fun getNavigatorScreenLifecycleProvider(screen: Screen): List<ScreenLifecycleContentProvider> {
    val navigatorScreenLifecycleProvider = LocalNavigatorScreenLifecycleProvider.current
    return remember(screen.key) {
        navigatorScreenLifecycleProvider.provide(screen)
    }
}

interface ScreenLifecycleProvider {

    fun getLifecycleOwner(): ScreenLifecycleOwner
}
