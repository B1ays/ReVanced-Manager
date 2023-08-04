package ru.blays.helios.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable

internal class DialogState {

    internal var isVisible = mutableStateOf(false)
        private set

    internal var isDismissible = mutableStateOf(true)
        private set

    fun show() {
        isVisible.value = true
        isDismissible.value = true
    }

    fun showNonDismissible() {
        isVisible.value = true
        isDismissible.value = false
    }

    fun hide() {
        isVisible.value = false
    }

    companion object {
        fun saver() = Saver<DialogState, Pair<Boolean, Boolean>>(
            save = {
                it.isVisible.value to it.isDismissible.value
            },
            restore = { (isVisible, isDismissible) ->
                DialogState().apply {
                    this.isVisible.value = isVisible
                    this.isDismissible.value = isDismissible
                }
            }
        )
    }
}

@Composable
internal fun rememberDialogState(): DialogState {
    return rememberSaveable(
        saver = DialogState.saver()
    ) {
        DialogState()
    }
}