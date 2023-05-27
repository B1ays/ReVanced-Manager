package ru.Blays.ReVanced.Manager.UI.Screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import ru.Blays.ReVanced.Manager.UI.Screens.destinations.VersionsListScreenDestination
import ru.Blays.ReVanced.Manager.UI.ViewModels.MainScreenViewModel
import ru.blays.revanced.Elements.DataClasses.Apps
import ru.blays.revanced.Elements.Elements.Screens.MainScreen.AppInfoCard
import ru.blays.revanced.Elements.Repository.SettingsRepository
import ru.blays.revanced.Elements.Util.getStringRes
import ru.blays.revanced.Presentation.R
import ru.hh.toolbar.custom_toolbar.CollapsingTitle
import ru.hh.toolbar.custom_toolbar.CustomToolbar

@RootNavGraph(start = true)
@Destination
@Composable
fun MainScreen(
    mainScreenViewModel: MainScreenViewModel = koinViewModel(),
    settingsRepository: SettingsRepository = koinInject(),
    navController: NavController
) {

    Scaffold(
        topBar = {
            CustomToolbar(
                collapsingTitle = CollapsingTitle.large(titleText = getStringRes(R.string.AppBar_Main))
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(top = padding.calculateTopPadding() + 40.dp)
                .fillMaxWidth()
        ) {
            items(Apps.values()) { app ->
                if (
                    (app == Apps.YOUTUBE && settingsRepository.youtubeManaged) ||
                    (app == Apps.YOUTUBE_MUSIC && settingsRepository.musicManaged) ||
                    (app == Apps.MICROG && settingsRepository.microGManaged)
                ) {
                    AppInfoCard(
                        app = app,
                        actionNavigateToVersionsListScreen = { navController.navigate(VersionsListScreenDestination(appType = it)) }
                    )
                }
            }
        }
    }
}

