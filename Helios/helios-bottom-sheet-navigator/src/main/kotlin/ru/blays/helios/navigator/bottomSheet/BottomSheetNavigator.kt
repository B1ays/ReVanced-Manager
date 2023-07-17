package ru.blays.helios.navigator.bottomSheet

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetNavigator(
    modifier: Modifier = Modifier,
    hideOnBackPress: Boolean = true,
    sheetShape: Shape = MaterialTheme.shapes.large,
    sheetElevation: Dp = BottomSheetDefaults.Elevation,
    sheetBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
    skipHalfExpanded: Boolean = false,
    key: String = compositionUniqueId(),
    sheetContent: BottomSheetNavigatorContent = { CurrentScreen() },
    content: BottomSheetNavigatorContent
) {

    lateinit var hideBottomSheet: () -> Unit

    val coroutineScope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState(skipHalfExpanded) { state ->
        when (state) {
            SheetValue.Hidden -> {
                hideBottomSheet()
                false
            }
            else -> true
        }
    }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
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

        hideBottomSheet = bottomSheetNavigator::hide

        CompositionLocalProvider(LocalBottomSheetNavigator provides bottomSheetNavigator) {
            BottomSheetScaffold(
                modifier = modifier,
                scaffoldState = scaffoldState,
                sheetShape = sheetShape,
                sheetTonalElevation = sheetElevation,
                containerColor = sheetBackgroundColor,
                sheetContentColor = sheetContentColor,
                sheetPeekHeight = 0.dp,
                sheetContent = {
                    BackHandler(enabled = scaffoldState.bottomSheetState.isVisible) {
                        if (bottomSheetNavigator.canPop && hideOnBackPress) {
                            hideBottomSheet()
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


@OptIn(ExperimentalMaterial3Api::class)
class BottomSheetNavigator internal constructor(
    private val navigator: Navigator,
    private val sheetState: SheetState,
    private val coroutineScope: CoroutineScope
) : Stack<Screen> by navigator {

    fun show(screen: Screen) {
        coroutineScope.launch {
            replaceAll(screen)
            sheetState.show()
            sheetState.expand()
        }
    }

    fun hide() {
        coroutineScope.launch {
            sheetState.hide()
            replaceAll(HiddenBottomSheetScreen)
        }
    }
}

private object HiddenBottomSheetScreen: Screen {

    @Composable
    override fun Content() {
        Spacer(modifier = Modifier.height(1.dp))
    }
}