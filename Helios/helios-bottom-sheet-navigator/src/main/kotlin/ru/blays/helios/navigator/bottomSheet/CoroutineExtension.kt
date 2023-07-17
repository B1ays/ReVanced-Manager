package ru.blays.helios.navigator.bottomSheet

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.blays.helios.core.Screen
import kotlin.coroutines.CoroutineContext

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
    showSuspend(CoroutineScope(context), screenFactory)
}