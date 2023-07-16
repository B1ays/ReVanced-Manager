package ru.blays.helios.navigator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.staticCompositionLocalOf
import ru.blays.helios.core.MultipleProvideBeforeScreenContent
import ru.blays.helios.core.Screen
import ru.blays.helios.core.ScreenLifecycleStore
import ru.blays.helios.core.ScreenModelStore
import ru.blays.helios.core.Stack
import ru.blays.helios.core.ThreadSafeMap
import ru.blays.helios.core.ThreadSafeSet
import ru.blays.helios.core.getNavigatorScreenLifecycleProvider
import ru.blays.helios.core.rememberScreenLifecycleOwner
import ru.blays.helios.core.toMutableStateStack

typealias NavigatorContent = @Composable (navigator: Navigator) -> Unit

typealias OnBackPressed = ((currentScreen: Screen) -> Boolean)?

val LocalNavigator: ProvidableCompositionLocal<Navigator?> =
    staticCompositionLocalOf { null }

val <T> ProvidableCompositionLocal<T?>.currentOrThrow: T
    @Composable
    get() = current ?: error("CompositionLocal is null")

@Composable
fun CurrentScreen() {
    val navigator = LocalNavigator.currentOrThrow
    val currentScreen = navigator.lastItem

    navigator.saveableState("currentScreen") {
        currentScreen.Content()
    }
}

@Composable
fun Navigator(
    screen: Screen,
    disposeBehavior: NavigatorDisposeBehavior = NavigatorDisposeBehavior(),
    onBackPressed: OnBackPressed = { true },
    key: String = compositionUniqueId(),
    content: NavigatorContent = { CurrentScreen() }
) {
    Navigator(
        screens = listOf(screen),
        disposeBehavior = disposeBehavior,
        onBackPressed = onBackPressed,
        key = key,
        content = content,
    )
}

@Composable
fun Navigator(
    screens: List<Screen>,
    disposeBehavior: NavigatorDisposeBehavior = NavigatorDisposeBehavior(),
    onBackPressed: OnBackPressed = { true },
    key: String = compositionUniqueId(),
    content: NavigatorContent = { CurrentScreen() },
) {
    require(screens.isNotEmpty()) { "Navigator must have at least one screen" }
    require(key.isNotEmpty()) { "Navigator key can't be empty" }

    CompositionLocalProvider(
        LocalNavigatorStateHolder providesDefault rememberSaveableStateHolder()
    ) {
        val navigator = rememberNavigator(screens, key, disposeBehavior, LocalNavigator.current)

        if (navigator.parent?.disposeBehavior?.disposeNestedNavigators != false) {
            NavigatorDisposableEffect(navigator)
        }

        CompositionLocalProvider(
            LocalNavigator provides navigator,
        ) {
            if (disposeBehavior.disposeSteps) {
                StepDisposableEffect(navigator)
            }

            NavigatorBackHandler(navigator, onBackPressed)

            content(navigator)
        }

        ChildrenNavigationDisposableEffect(navigator)
    }
}

class Navigator constructor(
    screens: List<Screen>,
    val key: String,
    private val stateHolder: SaveableStateHolder,
    val disposeBehavior: NavigatorDisposeBehavior,
    val parent: Navigator? = null
) : Stack<Screen> by screens.toMutableStateStack(minSize = 1) {

    val level: Int =
        parent?.level?.inc() ?: 0

    val lastItem: Screen by derivedStateOf {
        lastItemOrNull ?: error("Navigator has no screen")
    }

    private val stateKeys = ThreadSafeSet<String>()

    internal val children = ThreadSafeMap<NavigatorKey, Navigator>()

    @Deprecated(
        message = "Use 'lastItem' instead. Will be removed in 1.0.0.",
        replaceWith = ReplaceWith("lastItem")
    )
    val last: Screen by derivedStateOf {
        lastItem
    }

    @Composable
    fun saveableState(
        key: String,
        screen: Screen = lastItem,
        content: @Composable () -> Unit
    ) {
        val stateKey = "${screen.key}:$key"
        stateKeys += stateKey

        @Composable
        fun provideSaveableState(suffixKey: String, content: @Composable () -> Unit) {
            val providedStateKey = "$stateKey:$suffixKey"
            stateKeys += providedStateKey
            stateHolder.SaveableStateProvider(providedStateKey, content)
        }

        val lifecycleOwner = rememberScreenLifecycleOwner(screen)
        val navigatorScreenLifecycleOwners = getNavigatorScreenLifecycleProvider(screen)

        val composed = remember(lifecycleOwner, navigatorScreenLifecycleOwners) {
            listOf(lifecycleOwner) + navigatorScreenLifecycleOwners
        }
        MultipleProvideBeforeScreenContent(
            screenLifecycleContentProviders = composed,
            provideSaveableState = { suffix, content -> provideSaveableState(suffix, content) },
            content = {
                stateHolder.SaveableStateProvider(stateKey, content)
            }
        )
    }

    fun popUntilRoot() {
        popUntilRoot(this)
    }

    private tailrec fun popUntilRoot(navigator: Navigator) {
        navigator.popAll()

        if (navigator.parent != null) {
            popUntilRoot(navigator.parent)
        }
    }

    fun dispose(
        screen: Screen
    ) {
        ScreenModelStore.remove(screen)
        ScreenLifecycleStore.remove(screen)
        stateKeys
            .asSequence()
            .filter { it.startsWith(screen.key) }
            .forEach { key ->
                stateHolder.removeState(key)
                stateKeys -= key
            }
    }
}

data class NavigatorDisposeBehavior(
    val disposeNestedNavigators: Boolean = true,
    val disposeSteps: Boolean = true,
)

@Composable
fun compositionUniqueId(): String = currentCompositeKeyHash.toString(MaxSupportedRadix)

private val MaxSupportedRadix = 36
