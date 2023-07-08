package ru.Blays.ReVanced.Manager.UI.Navigation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import com.ramcosta.composedestinations.utils.isRouteOnBackStack
import ru.Blays.ReVanced.Manager.UI.Screens.NavGraphs
import ru.Blays.ReVanced.Manager.UI.Screens.destinations.AboutScreenDestination
import ru.Blays.ReVanced.Manager.UI.Screens.destinations.AppUpdateScreenDestination
import ru.Blays.ReVanced.Manager.UI.Screens.destinations.DirectionDestination
import ru.Blays.ReVanced.Manager.UI.Screens.destinations.LogViewerScreenDestination
import ru.Blays.ReVanced.Manager.UI.Screens.destinations.MainScreenDestination
import ru.Blays.ReVanced.Manager.UI.Screens.destinations.SettingsScreenDestination
import ru.Blays.ReVanced.Manager.UI.Screens.destinations.VersionsListScreenDestination
import ru.blays.revanced.Elements.Elements.FloatingBottomMenu.BottomBarItem
import ru.blays.revanced.Elements.Elements.FloatingBottomMenu.FloatingBottomBar
import ru.blays.revanced.Elements.GlobalState.NavBarExpandedContent.Companion.bottomNavBarExpandedContent
import ru.blays.revanced.Elements.GlobalState.NavBarState
import ru.blays.revanced.shared.LogManager.BLog

private const val TAG = "Navigator"

@Suppress("AnimateAsStateLabel")
@Composable
fun Navigator() {

    val navigationController = rememberNavController()

    val expandedContentState by bottomNavBarExpandedContent.collectAsState()

    val shouldHideNavigationBar = NavBarState.shouldHideNavigationBar

    val navBarHeightDp = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val navOffset by animateDpAsState(if (shouldHideNavigationBar) 80.dp + navBarHeightDp else 0.dp)

    val destinationsList = listOf(
        BottomBarItem.Icon(
            icon = Icons.Rounded.Home,
            description = null,
            id = MainScreenDestination.route
        ) {
            bottomNavNarNavigate(navigationController, destination = MainScreenDestination)
        },
        BottomBarItem.Icon(
            icon = Icons.Rounded.Settings,
            description = null,
            id = SettingsScreenDestination.route
        ) {
            bottomNavNarNavigate(navigationController, SettingsScreenDestination)
        }
    )

    val selectedItem = when (navigationController.currentDestinationAsState().value?.route) {
        MainScreenDestination.route -> 0
        SettingsScreenDestination.route -> 1
        VersionsListScreenDestination.route -> 0
        AboutScreenDestination.route -> 1
        LogViewerScreenDestination.route -> 1
        AppUpdateScreenDestination.route -> 0
        else -> 0
    }

    Scaffold(
        bottomBar = {
            FloatingBottomBar(
                expanded = expandedContentState.isExpanded,
                selectedItem = selectedItem,
                items = destinationsList,
                expandedContent = expandedContentState.content,
                modifier = Modifier.offset(y = navOffset)
            )
        }
    ) { padding ->

        DestinationsNavHost(
            modifier = Modifier.padding(top = padding.calculateTopPadding()),
            navGraph = NavGraphs.root,
            navController = navigationController,
            dependenciesContainerBuilder = {
                dependency(navigationController)
            }
        )
    }
}

private fun bottomNavNarNavigate(navController: NavController, destination: DirectionDestination) {
    BLog.i(TAG, "Navigate to: ${destination.route}")
    with(navController) {
        if (isRouteOnBackStack(route = destination)) {
            popBackStack(route = destination.route, inclusive = false)
        } else {
            navigate(destination)
        }
    }
}