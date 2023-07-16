package ru.blays.helios.core

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

val LocalNavigatorScreenLifecycleProvider: ProvidableCompositionLocal<NavigatorScreenLifecycleProvider> =
    staticCompositionLocalOf { DefaultNavigatorScreenLifecycleProvider() }

/**
 * Can provides a list of ScreenLifecycleOwner for each Screen in the Navigator stack.
 */
interface NavigatorScreenLifecycleProvider {

    fun provide(screen: Screen): List<ScreenLifecycleContentProvider>
}

internal class DefaultNavigatorScreenLifecycleProvider() : NavigatorScreenLifecycleProvider {
    override fun provide(screen: Screen): List<ScreenLifecycleContentProvider> {
        return listOf(AndroidScreenLifecycleOwner.get(screen))
    }
}
