package ru.blays.revanced.Presentation.Screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import ru.blays.revanced.Presentation.Elements.Screens.MainScreen.AppInfoCard
import ru.blays.revanced.Presentation.R
import ru.blays.revanced.Presentation.Screens.destinations.VersionsListScreenDestination
import ru.blays.revanced.Presentation.ViewModels.MainScreen.MainScreenViewModel
import ru.hh.toolbar.custom_toolbar.CollapsingTitle
import ru.hh.toolbar.custom_toolbar.CustomToolbar

@RootNavGraph(start = true)
@Destination
@Composable
fun MainScreen(
    mainScreenViewModel: MainScreenViewModel,
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
                    icon = app.ico.invoke(),
                    title = app.name,
                    currentVersion = "1.10.1",
                    availableVersion = "1.12.2",
                    onClick = app.action
                )
            }
        }
    }
}

private class Apps(navController: NavController) {

    data class Params(
        val name: String,
        val ico: @Composable () -> ImageVector,
        val action: () -> Unit
    )

    val list = listOf(
        Params(
            name = "YouTube ReVanced",
            ico = { ImageVector.vectorResource(id = R.drawable.youtube_monochrome) }
        ) {
            navController.navigate(VersionsListScreenDestination)
        },
        Params(
            name = "Music ReVanced",
            ico = { ImageVector.vectorResource(R.drawable.music_monochrome) }
        ) {
            navController.navigate(VersionsListScreenDestination)
        }
    )
}

