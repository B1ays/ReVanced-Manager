package ru.Blays.ReVanced.Manager.Utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.blays.helios.core.Screen
import ru.blays.helios.core.Stack
import ru.blays.helios.navigator.bottomSheet.BottomSheetNavigator
import kotlin.coroutines.CoroutineContext

/**
* Push new screen with async [Screen] build, using existing [CoroutineScope]
**/
fun <T: Screen> Stack<T>.pushSuspend(scope: CoroutineScope, screenFactory: suspend CoroutineScope.() -> T) {
    scope.launch {
        val screen = screenFactory()
        push(screen)
    }
}

/**
 * Push new screen with async [Screen] build, using new [CoroutineScope] with adjusted [CoroutineContext]
**/
fun <T: Screen> Stack<T>.pushSuspend(context: CoroutineContext, screenFactory: suspend CoroutineScope.() -> T) {
    CoroutineScope(context).launch {
        val screen = screenFactory()
        push(screen)
    }
}

/**
 * Show new BottomSheet with async [Screen] build, using existing [CoroutineScope]
 **/
fun <T: Screen> BottomSheetNavigator.showSuspend(scope: CoroutineScope, screenFactory: suspend CoroutineScope.() -> T) {
    scope.launch {
        val screen = screenFactory()
        show(screen)
    }
}

/**
 * Show new BottomSheet with async [Screen] build, using new [CoroutineScope] with adjusted [CoroutineContext]
 **/
fun <T: Screen> BottomSheetNavigator.showSuspend(context: CoroutineContext, screenFactory: suspend CoroutineScope.() -> T) {
    CoroutineScope(context).launch {
        val screen = screenFactory()
        show(screen)
    }
}