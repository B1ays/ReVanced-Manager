package ru.blays.helios.core

import androidx.compose.runtime.Composable

interface ScreenLifecycleOwner : ScreenLifecycleContentProvider, ScreenDisposable

interface ScreenLifecycleContentProvider {
    /**
     * Called before rendering the Screen Content.
     *
     * IMPORTANT: This is only called when ScreenLifecycleOwner is provided by [ScreenLifecycleProvider] or [NavigatorScreenLifecycleProvider].
     */
    @Composable
    fun ProvideBeforeScreenContent(
        provideSaveableState: @Composable (suffixKey: String, content: @Composable () -> Unit) -> Unit,
        content: @Composable () -> Unit
    ): Unit = content()
}

interface ScreenDisposable {
    /**
     * Called on the Screen leaves the stack.
     */
    fun onDispose(screen: Screen) {}
}

object DefaultScreenLifecycleOwner : ScreenLifecycleOwner
