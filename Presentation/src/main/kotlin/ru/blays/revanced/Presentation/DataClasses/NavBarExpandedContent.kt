package ru.blays.revanced.Presentation.DataClasses

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow

data class NavBarExpandedContent(
    val isExpanded: Boolean = false,
    val content: @Composable () -> Unit = {}
) {
    companion object {
        val bottomNavBarExpandedContent = MutableStateFlow(NavBarExpandedContent())
    }
}