package ru.blays.helios.navigator

import ru.blays.helios.core.ThreadSafeMap
import kotlin.reflect.KType
import kotlin.reflect.typeOf

typealias NavigatorKey = String

object NavigatorLifecycleStore {

    private val owners = ThreadSafeMap<NavigatorKey, ThreadSafeMap<KType, NavigatorDisposable>>()

    /**
     * Register a NavigatorDisposable that will be called `onDispose` on the
     * [navigator] leaves the Composition.
     */
    inline fun <reified T : NavigatorDisposable> register(
        navigator: Navigator,
        noinline factory: (NavigatorKey) -> T,
    ): T {
        return register(navigator, typeOf<T>(), factory) as T
    }

    @PublishedApi
    internal fun <T : NavigatorDisposable> register(
        navigator: Navigator,
        screenDisposeListenerType: KType,
        factory: (NavigatorKey) -> T,
    ): NavigatorDisposable {
        return owners.getOrPut(navigator.key) {
            ThreadSafeMap<KType, NavigatorDisposable>().apply {
                put(screenDisposeListenerType, factory(navigator.key))
            }
        }.getOrPut(screenDisposeListenerType) {
            factory(navigator.key)
        }
    }

    fun remove(navigator: Navigator) {
        owners.remove(navigator.key)?.forEach { it.value.onDispose(navigator) }
    }
}
