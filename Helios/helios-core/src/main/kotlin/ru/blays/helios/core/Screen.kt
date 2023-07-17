package ru.blays.helios.core

import androidx.compose.runtime.Composable
import java.io.Serializable

interface Screen: Serializable {

    val key: ScreenKey
        get() = commonKeyGeneration()

    @Composable
    fun Content()
}

internal fun Screen.commonKeyGeneration() =
    this::class.qualifiedName ?: error("Default ScreenKey not found, please provide your own key")
