package ru.Blays.ReVanced.Manager.UI.Navigation

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.ext.getFullName
import ru.Blays.ReVanced.Manager.UI.Screens.AboutScreen
import ru.Blays.ReVanced.Manager.UI.Screens.AppUpdateScreen
import ru.Blays.ReVanced.Manager.UI.Screens.LogViewerScreen
import ru.Blays.ReVanced.Manager.UI.Screens.MainScreen
import ru.Blays.ReVanced.Manager.UI.Screens.SettingsScreen
import ru.Blays.ReVanced.Manager.UI.Screens.VersionsListScreen
import ru.blays.helios.core.Screen
import ru.blays.helios.dialogs.DialogNavigator
import ru.blays.helios.navigator.Navigator
import ru.blays.helios.navigator.bottomSheet.BottomSheetNavigator
import ru.blays.helios.transitions.FadeTransition
import ru.blays.revanced.Elements.Elements.FloatingBottomMenu.BottomBarItem
import ru.blays.revanced.Elements.Elements.FloatingBottomMenu.FloatingBottomBar
import ru.blays.revanced.shared.LogManager.BLog

private const val TAG = "Navigator"

var shouldHideNavigationBar by mutableStateOf(false)

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Suppress("AnimateAsStateLabel")
@Composable
fun Navigator() {

    val navBarHeightDp = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val navOffset by animateDpAsState(if (shouldHideNavigationBar) 80.dp + navBarHeightDp else 0.dp)

    DialogNavigator { _ ->
        BottomSheetNavigator { _ ->
            Navigator(
                screen = MainScreen()
            ) { navigator ->

                val destinationsList = rememberDestinationsList(destinationsList(navigator))

                val selectedItem = when (navigator.lastItem) {
                    is MainScreen -> 0
                    is VersionsListScreen -> 0
                    is AppUpdateScreen -> 0
                    is SettingsScreen -> 1
                    is AboutScreen -> 1
                    is LogViewerScreen -> 1
                    else -> 0
                }

                Scaffold(
                    bottomBar = {
                        FloatingBottomBar(
                            expanded = false,
                            selectedItem = selectedItem,
                            items = destinationsList,
                            expandedContent = { },
                            modifier = Modifier.offset(y = navOffset)
                        )
                    }
                ) {
                    FadeTransition(navigator)
                }
            }
        }
    }
}

private inline fun <reified T: Screen> bottomNavbarNavigate(navigator: Navigator, destination: T) = with(navigator) {
    BLog.d(TAG, "Navigate to: ${T::class.simpleName}")
    if (
        items.any { it::class == T::class }
    ) {
        popUntil { screen ->
            screen::class == destination::class
        }
    } else {
        push(destination)
    }
}

@NonRestartableComposable
@SuppressLint("ComposableNaming")
@Composable
fun rememberDestinationsList(list: List<BottomBarItem.Icon>): List<BottomBarItem.Icon> {
    return remember(list) {
        list
    }
}

val destinationsList: (Navigator) -> List<BottomBarItem.Icon> = {
    listOf(
        BottomBarItem.Icon(
            icon = Icons.Rounded.Home,
            description = null,
            id = MainScreen::class.getFullName()
        ) {
            bottomNavbarNavigate(it, MainScreen())
        },
        BottomBarItem.Icon(
            icon = Icons.Rounded.Settings,
            description = null,
            id = SettingsScreen::class.getFullName()
        ) {
            bottomNavbarNavigate(it, SettingsScreen())
        }
    )
}