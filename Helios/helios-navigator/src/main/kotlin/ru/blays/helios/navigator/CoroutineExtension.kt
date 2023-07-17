package ru.blays.helios.navigator

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.blays.helios.core.Screen
import kotlin.coroutines.CoroutineContext

/**
 * Push new screen with async [Screen] build, using existing [CoroutineScope]
 **/
fun <T: Screen> Navigator.pushSuspend(scope: CoroutineScope, screenFactory: suspend CoroutineScope.() -> T) {
    scope.launch {
        val screen = screenFactory()
        push(screen)
    }
}

/**
 * Push new screen with async [Screen] build, using new [CoroutineScope] with adjusted [CoroutineContext]
 **/
fun <T: Screen> Navigator.pushSuspend(context: CoroutineContext, screenFactory: suspend CoroutineScope.() -> T) {
    pushSuspend(CoroutineScope(context), screenFactory)
}