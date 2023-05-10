package ru.Blays.ReVanced.Manager.ui.Navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import ru.Blays.ReVanced.Manager.di.DependencyContainer
import ru.blays.revanced.Presentation.DataClasses.NavBarExpandedContent.Companion.bottomNavBarExpandedContent
import ru.blays.revanced.Presentation.Screens.NavGraphs
import ru.blays.revanced.Presentation.Screens.destinations.MainScreenDestination
import ru.blays.revanced.Presentation.Screens.destinations.SettingsScreenDestination
import ru.blays.timetable.UI.Compose.ComposeElements.FloatingBottomMenu.BottomBarItem
import ru.blays.timetable.UI.Compose.ComposeElements.FloatingBottomMenu.FloatingBottomBar


@Composable
fun Navigator(dependencyContainer: DependencyContainer) {

    val navigationController = rememberNavController()

    val expandedContentState by bottomNavBarExpandedContent.collectAsState()

    val destinationsList = listOf(
        BottomBarItem.Icon(
            icon = Icons.Rounded.Home,
            description = null,
            id = MainScreenDestination.route
        ) {
            navigationController.navigate(MainScreenDestination)
        },
        BottomBarItem.Icon(
            icon = Icons.Rounded.Settings,
            description = null,
            id = SettingsScreenDestination.route
        ) {
            navigationController.navigate(SettingsScreenDestination)
        }
    )

    Scaffold(
        bottomBar = {
            FloatingBottomBar(
                expanded = expandedContentState.isExpanded,
                selectedItem = when (navigationController.currentDestinationAsState().value?.route) {
                    MainScreenDestination.route -> 0
                    SettingsScreenDestination.route -> 1
                    else -> 0
                },
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
                dependency(dependencyContainer.mainScreenViewModel)
                dependency(dependencyContainer.versionsListScreenViewModel)
                dependency(navigationController)
            }
        )
    }
}