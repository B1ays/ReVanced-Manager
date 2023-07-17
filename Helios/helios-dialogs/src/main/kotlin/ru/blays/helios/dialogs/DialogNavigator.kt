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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.blays.helios.core.Screen
import ru.blays.helios.core.Stack
import ru.blays.helios.navigator.CurrentScreen
import ru.blays.helios.navigator.Navigator
import ru.blays.helios.navigator.compositionUniqueId

typealias DialogNavigatorContent = @Composable (dialogNavigator: DialogNavigator) -> Unit

val LocalDialogNavigator: ProvidableCompositionLocal<DialogNavigator> =
    staticCompositionLocalOf { error("BottomSheetNavigator not initialized") }

@Suppress("LocalVariableName")
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

    val _isDismissRequestEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val isDismissRequestEnabled by _isDismissRequestEnabled.collectAsState()

    val _dialogState: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val dialogState by _dialogState.collectAsState()

    Navigator(
        screen = EmptyDialog,
        onBackPressed = {true},
        key = key,

    ) { navigator ->
        val dialogNavigator = remember(
            navigator,
            _dialogState,
            _isDismissRequestEnabled,
            coroutineScope
        ) {
            DialogNavigator(
                navigator,
                _dialogState,
                _isDismissRequestEnabled,
                coroutineScope
            )
        }

        closeDialog = dialogNavigator::hide

        CompositionLocalProvider(LocalDialogNavigator provides dialogNavigator) {
            content(dialogNavigator)
            if (dialogState) {
                AlertDialog(
                    onDismissRequest = closeDialog,
                    properties = DialogProperties(
                        dismissOnBackPress = isDismissRequestEnabled,
                        dismissOnClickOutside = isDismissRequestEnabled,
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
    private val dialogState: MutableStateFlow<Boolean>,
    private val isDismissRequestEnabled: MutableStateFlow<Boolean>,
    private val coroutineScope: CoroutineScope
): Stack<Screen> by navigator {

    fun show(screen: Screen) {
        coroutineScope.launch {
            isDismissRequestEnabled.`true`()
            replaceAll(screen)
            dialogState.`true`()
        }
    }

    fun showNonDismissible(screen: Screen) {
        coroutineScope.launch {
            isDismissRequestEnabled.`false`()
            replaceAll(screen)
            dialogState.`true`()
        }
    }

    fun hide() {
        coroutineScope.launch {
            dialogState.`false`()
            popAll()
            replaceAll(EmptyDialog)
        }
    }

}

private object EmptyDialog: Screen {
    @Composable
    override fun Content() {
        Spacer(modifier = Modifier.height(1.dp))
    }
}

private suspend fun MutableStateFlow<Boolean>.`false`() {
    emit(false)
}

private suspend fun MutableStateFlow<Boolean>.`true`() {
    emit(true)
}