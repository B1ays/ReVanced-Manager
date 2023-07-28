package ru.blays.helios.dialogs

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.blays.helios.core.Screen
import ru.blays.helios.core.Stack
import ru.blays.helios.navigator.CurrentScreen
import ru.blays.helios.navigator.Navigator
import ru.blays.helios.navigator.compositionUniqueId

typealias DialogNavigatorContent = @Composable (dialogNavigator: DialogNavigator) -> Unit

val LocalDialogNavigator: ProvidableCompositionLocal<DialogNavigator> =
    staticCompositionLocalOf { error("BottomSheetNavigator not initialized") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogNavigator(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(containerColor),
    elevation: CardElevation = CardDefaults.elevatedCardElevation(),
    shape: Shape = CardDefaults.elevatedShape,
    key: String = compositionUniqueId(),
    dialogContent: DialogNavigatorContent = { CurrentScreen() },
    content: DialogNavigatorContent
) {

    lateinit var closeDialog: () -> Unit

    val coroutineScope = rememberCoroutineScope()

    val dialogState = rememberDialogState()

    Navigator(
        screen = EmptyDialog,
        onBackPressed = {true},
        key = key,

    ) { navigator ->
        val dialogNavigator = remember(
            navigator,
            dialogState,
            coroutineScope
        ) {
            DialogNavigator(
                navigator,
                dialogState,
                coroutineScope
            )
        }

        closeDialog = dialogNavigator::hide

        CompositionLocalProvider(LocalDialogNavigator provides dialogNavigator) {
            content(dialogNavigator)
            if (dialogState.isVisible.value) {
                AlertDialog(
                    onDismissRequest = closeDialog,
                    properties = DialogProperties(
                        dismissOnBackPress = dialogState.isDismissible.value,
                        dismissOnClickOutside = dialogState.isDismissible.value,
                        securePolicy = SecureFlagPolicy.SecureOff
                    )
                ) {
                    ElevatedCard(
                        modifier = modifier,
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = containerColor,
                            contentColor = contentColor
                        ),
                        elevation = elevation,
                        shape = shape
                    ) {
                        dialogContent(dialogNavigator)
                    }
                }
            }
        }
    }
}

class DialogNavigator internal constructor(
    private val navigator: Navigator,
    private val dialogState: DialogState,
    private val coroutineScope: CoroutineScope
): Stack<Screen> by navigator {

    fun show(screen: Screen) {
        coroutineScope.launch {
            replaceAll(screen)
            dialogState.show()
        }
    }

    fun showNonDismissible(screen: Screen) {
        coroutineScope.launch {
            replaceAll(screen)
            dialogState.showNonDismissible()
        }
    }

    fun hide() {
        coroutineScope.launch {
            popAll()
            replaceAll(EmptyDialog)
            dialogState.hide()
        }
    }
}

private object EmptyDialog: Screen {
    @Composable
    override fun Content() {
        Spacer(modifier = Modifier.height(1.dp))
    }
}