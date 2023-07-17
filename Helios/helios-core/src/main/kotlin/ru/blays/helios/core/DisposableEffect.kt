package ru.blays.helios.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffectResult
import androidx.compose.runtime.DisposableEffectScope
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.remember

@Composable
@NonRestartableComposable
fun DisposableEffectIgnoringConfiguration(
    key1: Any?,
    effect: DisposableEffectScope.() -> DisposableEffectResult
) {
    val configurationChecker = getConfigurationChecker()
    remember(configurationChecker, key1) { DisposableEffectIgnoringConfigurationImpl(configurationChecker, effect) }
}

private val InternalDisposableEffectScope = DisposableEffectScope()

private class DisposableEffectIgnoringConfigurationImpl(
    private val configurationChecker: ConfigurationChecker,
    private val effect: DisposableEffectScope.() -> DisposableEffectResult
) : RememberObserver {
    private var onDispose: DisposableEffectResult? = null

    override fun onRemembered() {
        onDispose = InternalDisposableEffectScope.effect()
    }

    override fun onForgotten() {
        onDispose?.takeUnless { configurationChecker.isChangingConfigurations() }?.dispose()
        onDispose = null
    }

    override fun onAbandoned() {
        // Nothing to do as [onRemembered] was not called.
    }
}
