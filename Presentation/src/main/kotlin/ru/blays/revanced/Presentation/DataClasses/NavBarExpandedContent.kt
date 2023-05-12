package ru.blays.revanced.Presentation.DataClasses

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow

data class NavBarExpandedContent(
    val isExpanded: Boolean = false,
    val content: @Composable () -> Unit = {}
) {
    companion object {
        val bottomNavBarExpandedContent = MutableStateFlow(NavBarExpandedContent())

        fun hide() {
            if (bottomNavBarExpandedContent.value.isExpanded) {
                bottomNavBarExpandedContent.tryEmit(
                    NavBarExpandedContent(
                        isExpanded = false
                    )
                )
            }
        }

        fun setContent(content: @Composable () -> Unit) {
            bottomNavBarExpandedContent.tryEmit(
                NavBarExpandedContent(
                    isExpanded = true,
                    content = content
                )
            )
        }
    }
}