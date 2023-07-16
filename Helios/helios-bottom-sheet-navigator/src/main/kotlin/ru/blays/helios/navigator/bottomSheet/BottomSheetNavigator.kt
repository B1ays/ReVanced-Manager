package ru.blays.helios.navigator.bottomSheet

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetDefaults
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.contentColorFor
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.blays.helios.core.Screen
import ru.blays.helios.core.Stack
import ru.blays.helios.navigator.CurrentScreen
import ru.blays.helios.navigator.Navigator
import ru.blays.helios.navigator.compositionUniqueId

typealias BottomSheetNavigatorContent = @Composable (bottomSheetNavigator: BottomSheetNavigator) -> Unit

val LocalBottomSheetNavigator: ProvidableCompositionLocal<BottomSheetNavigator> =
    staticCompositionLocalOf { error("BottomSheetNavigator not initialized") }

@ExperimentalMaterialApi
@Composable
fun BottomSheetNavigator(
    modifier: Modifier = Modifier,
    hideOnBackPress: Boolean = true,
    scrimColor: Color = ModalBottomSheetDefaults.scrimColor,
    sheetShape: Shape = MaterialTheme.shapes.large,
    sheetElevation: Dp = ModalBottomSheetDefaults.Elevation,
    sheetBackgroundColor: Color = MaterialTheme.colors.surface,
    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
    skipHalfExpanded: Boolean = true,
    key: String = compositionUniqueId(),
    sheetContent: BottomSheetNavigatorContent = { CurrentScreen() },
    content: BottomSheetNavigatorContent
) {
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = skipHalfExpanded
    )

    Navigator(
        screen = HiddenBottomSheetScreen,
        onBackPressed = null,
        key = key
    ) { navigator ->
        val bottomSheetNavigator = remember(
            navigator,
            sheetState,
            coroutineScope
        ) {
            BottomSheetNavigator(
                navigator,
                sheetState,
                coroutineScope
            )
        }

        CompositionLocalProvider(LocalBottomSheetNavigator provides bottomSheetNavigator) {
            ModalBottomSheetLayout(
                modifier = modifier,
                scrimColor = scrimColor,
                sheetState = sheetState,
                sheetShape = sheetShape,
                sheetElevation = sheetElevation,
                sheetBackgroundColor = sheetBackgroundColor,
                sheetContentColor = sheetContentColor,
                sheetContent = {
                    BackHandler(enabled = sheetState.isVisible) {
                        if (bottomSheetNavigator.pop().not() && hideOnBackPress) {
                            bottomSheetNavigator.hide()
                        }
                    }
                    sheetContent(bottomSheetNavigator)
                },
                content = {
                    content(bottomSheetNavigator)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
class BottomSheetNavigator internal constructor(
    private val navigator: Navigator,
    private val sheetState: ModalBottomSheetState,
    private val coroutineScope: CoroutineScope
) : Stack<Screen> by navigator {

    fun show(screen: Screen) {
        coroutineScope.launch {
            replaceAll(screen)
            sheetState.show()
        }
    }

    fun hide() {
        coroutineScope.launch {
            sheetState.hide()
            replaceAll(HiddenBottomSheetScreen)
        }
    }
}

private object HiddenBottomSheetScreen : Screen {

    @Composable
    override fun Content() {
        Spacer(modifier = Modifier.height(1.dp))
    }
}
