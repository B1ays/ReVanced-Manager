package ru.blays.helios.navigator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import ru.blays.helios.core.DisposableEffectIgnoringConfiguration
import ru.blays.helios.core.StackEvent

interface NavigatorDisposable {
    fun onDispose(navigator: Navigator)
}

private val disposableEvents: Set<StackEvent> =
    setOf(StackEvent.Pop, StackEvent.Replace)

@Composable
internal fun NavigatorDisposableEffect(
    navigator: Navigator
) {
    DisposableEffectIgnoringConfiguration(navigator) {
        onDispose {
            disposeNavigator(navigator)
        }
    }
}

@Composable
internal fun StepDisposableEffect(
    navigator: Navigator
) {
    val currentScreens = navigator.items

    DisposableEffect(currentScreens) {
        onDispose {
            val newScreenKeys = navigator.items.map { it.key }
            if (navigator.lastEvent in disposableEvents) {
                currentScreens.filter { it.key !in newScreenKeys }.forEach {
                    navigator.dispose(it)
                }
                navigator.clearEvent()
            }
        }
    }
}

@Composable
internal fun ChildrenNavigationDisposableEffect(
    navigator: Navigator
) {
    // disposing children navigators
    DisposableEffectIgnoringConfiguration(navigator) {
        onDispose {
            fun disposeChildren(navigator: Navigator) {
                disposeNavigator(navigator)
                navigator.children.values.forEach { childNavigator ->
                    disposeChildren(childNavigator)
                }
                navigator.children.clear()
            }
            if (navigator.parent == null || navigator.disposeBehavior.disposeNestedNavigators) {
                navigator.children.values.forEach { childNavigator ->
                    disposeChildren(childNavigator)
                }
            }
        }
    }

    // referencing nested navigators in parent navigator
    DisposableEffectIgnoringConfiguration(navigator) {
        navigator.parent?.children?.put(navigator.key, navigator)
        onDispose {
            if (navigator.parent?.disposeBehavior?.disposeNestedNavigators != false) {
                navigator.parent?.children?.remove(navigator.key)
            }
        }
    }
}

internal fun disposeNavigator(navigator: Navigator) {
    for (screen in navigator.items) {
        navigator.dispose(screen)
    }
    NavigatorLifecycleStore.remove(navigator)
    navigator.clearEvent()
}
