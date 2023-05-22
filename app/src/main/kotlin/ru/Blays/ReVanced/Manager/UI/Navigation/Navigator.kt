package ru.Blays.ReVanced.Manager.UI.Navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import com.ramcosta.composedestinations.utils.isRouteOnBackStack
import ru.Blays.ReVanced.Manager.UI.Screens.NavGraphs
import ru.Blays.ReVanced.Manager.UI.Screens.destinations.DirectionDestination
import ru.Blays.ReVanced.Manager.UI.Screens.destinations.MainScreenDestination
import ru.Blays.ReVanced.Manager.UI.Screens.destinations.SettingsScreenDestination
import ru.blays.revanced.Elements.DataClasses.NavBarExpandedContent.Companion.bottomNavBarExpandedContent
import ru.blays.revanced.Elements.Elements.FloatingBottomMenu.BottomBarItem
import ru.blays.revanced.Elements.Elements.FloatingBottomMenu.FloatingBottomBar

@Composable
fun Navigator() {

    val navigationController = rememberNavController()

    val expandedContentState by bottomNavBarExpandedContent.collectAsState()

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
        else -> 0
    }

    Scaffold(
        bottomBar = {
            FloatingBottomBar(
                expanded = expandedContentState.isExpanded,
                selectedItem = selectedItem,
                items = destinationsList,
                expandedContent = expandedContentState.content
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
    with(navController) {
        if (isRouteOnBackStack(route = destination)) {
            popBackStack(route = destination.route, inclusive = false)
        } else {
            navigate(destination)
        }
    }
}