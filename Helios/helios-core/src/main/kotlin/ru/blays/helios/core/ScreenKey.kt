package ru.blays.helios.core

import java.util.UUID

typealias ScreenKey = String

val Screen.uniqueScreenKey: ScreenKey
    get() = "Screen#${UUID.randomUUID()}"
