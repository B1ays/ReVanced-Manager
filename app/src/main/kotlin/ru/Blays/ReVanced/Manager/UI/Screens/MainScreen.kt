package ru.Blays.ReVanced.Manager.UI.Screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.pullrefresh.pullRefresh
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import ru.hh.toolbar.custom_toolbar.rememberToolbarScrollBehavior

@OptIn(ExperimentalStdlibApi::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun MainScreen(
    viewModel: MainScreenViewModel = koinViewModel(),
    settingsRepository: SettingsRepository = koinInject(),
    navController: NavController
) {

    LaunchedEffect(key1 = Unit) {
        viewModel.onRefresh()
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = viewModel.isRefreshing,
        onRefresh = viewModel::onRefresh
    )

    val scrollBehavior = rememberToolbarScrollBehavior()

    Scaffold(
        topBar = {
            CustomToolbar(
                collapsingTitle = CollapsingTitle.large(titleText = getStringRes(R.string.AppBar_Main)),
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .pullRefresh(pullRefreshState)
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(top = padding.calculateTopPadding() + 40.dp)
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                items(Apps.entries) { app ->
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
            PullRefreshIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter),
                refreshing = viewModel.isRefreshing,
                state = pullRefreshState,
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}

