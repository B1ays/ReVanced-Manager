package ru.blays.helios.navigator

import android.os.Parcelable
import androidx.compose.runtime.saveable.listSaver
import ru.blays.helios.core.Screen

/**
* Navigator Saver that forces all Screens be [Parcelable], if not, it will throw a exception while trying to save
* the navigator state.
*/
@Suppress("UNCHECKED_CAST")
fun parcelableNavigatorSaver(): NavigatorSaver<Any> = NavigatorSaver { _, key, stateHolder, disposeBehavior, parent ->
    listSaver(
        save = { navigator ->
            val screenAsParcelables = navigator.items.filterIsInstance<Parcelable>()

            if(navigator.items.size > screenAsParcelables.size) {
                val screensNotParcelable = navigator.items
                    .filterNot { screen -> screenAsParcelables.any { screen == it } }
                    .map { it::class.qualifiedName }
                    .joinToString()

                throw NavigatorSaverException("Unable to save instance state for Screens: $screensNotParcelable. Implement android.os.Parcelable on your Screen.")
            }

            screenAsParcelables
        },
        restore = { items -> Navigator(items as List<Screen>, key, stateHolder, disposeBehavior, parent) }
    )
}

class NavigatorSaverException(message: String) : RuntimeException(message)
