package ru.blays.revanced.Elements.GlobalState

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object NavBarState {
    var shouldHideNavigationBar by mutableStateOf(false)
}