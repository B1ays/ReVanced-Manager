package ru.blays.revanced.Presentation.Screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import com.theapache64.rebugger.Rebugger
import org.koin.androidx.compose.koinViewModel
import ru.blays.revanced.Presentation.Elements.Screens.MainScreen.AppInfoCard
import ru.blays.revanced.Presentation.Elements.VectorImages.AppsIcons
import ru.blays.revanced.Presentation.Elements.VectorImages.appsicons.MusicMonochrome
import ru.blays.revanced.Presentation.Elements.VectorImages.appsicons.YoutubeMonochrome
import ru.blays.revanced.Presentation.Screens.destinations.VersionsListScreenDestination
import ru.blays.revanced.Presentation.ViewModels.MainScreen.MainScreenViewModel
import ru.hh.toolbar.custom_toolbar.CollapsingTitle
import ru.hh.toolbar.custom_toolbar.CustomToolbar

@RootNavGraph(start = true)
@Destination
@Composable
fun MainScreen(
    mainScreenViewModel: MainScreenViewModel = koinViewModel(),
    navController: NavController
) {

    val appList = Apps(navController = navController).list


    Scaffold(
        topBar = {
            CustomToolbar(
                collapsingTitle = CollapsingTitle.large(titleText = "Главная")
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(top = padding.calculateTopPadding() + 40.dp)
                .fillMaxWidth()
        ) {
            items(appList) {app ->
                AppInfoCard(
                    icon = app.ico,
                    title = app.name,
                    currentVersion = "1.10.1",
                    availableVersion = "1.12.2",
                    onClick = app.action
                )
            }
        }
    }
    Rebugger(
        trackMap = mapOf(
            "mainScreenViewModel" to mainScreenViewModel,
            "navController" to navController,
            "appList" to appList,
        ),
    )
}

private class Apps(navController: NavController) {

    data class Params(
        val name: String,
        val ico: ImageVector,
        val action: () -> Unit
    )

    val list = listOf(
        Params(
            name = "YouTube ReVanced",
            ico = AppsIcons.YoutubeMonochrome
        ) {
            navController.navigate(VersionsListScreenDestination)
        },
        Params(
            name = "Music ReVanced",
            ico = AppsIcons.MusicMonochrome
        ) {
            navController.navigate(VersionsListScreenDestination)
        }
    )
}

