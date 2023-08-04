package ru.blays.helios.navigator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.staticCompositionLocalOf
import ru.blays.helios.core.Screen

internal val LocalNavigatorStateHolder: ProvidableCompositionLocal<SaveableStateHolder> =
    staticCompositionLocalOf { error("LocalNavigatorStateHolder not initialized") }

@Composable
internal fun rememberNavigator(
    screens: List<Screen>,
    key: String,
    disposeBehavior: NavigatorDisposeBehavior,
    parent: Navigator?,
): Navigator {
    val stateHolder = LocalNavigatorStateHolder.current
    // TODO("Return screen restore")
    /*val navigatorSaver = LocalNavigatorSaver.current
    val saver = remember(
        navigatorSaver,
        stateHolder,
        parent,
        disposeBehavior
    ) {
        navigatorSaver.saver(
            screens,
            key,
            stateHolder,
            disposeBehavior,
            parent
        )
    }*/

    return remember(key) {
        Navigator(screens, key, stateHolder, disposeBehavior, parent)
    }
}