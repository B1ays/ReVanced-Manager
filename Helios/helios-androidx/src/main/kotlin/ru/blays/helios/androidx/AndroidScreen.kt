package ru.blays.helios.androidx

import ru.blays.helios.core.DefaultScreenLifecycleOwner
import ru.blays.helios.core.Screen
import ru.blays.helios.core.ScreenKey
import ru.blays.helios.core.ScreenLifecycleOwner
import ru.blays.helios.core.ScreenLifecycleProvider
import ru.blays.helios.core.uniqueScreenKey

abstract class AndroidScreen : Screen, ScreenLifecycleProvider {

    override val key: ScreenKey = uniqueScreenKey

    override fun getLifecycleOwner(): ScreenLifecycleOwner = DefaultScreenLifecycleOwner
}
