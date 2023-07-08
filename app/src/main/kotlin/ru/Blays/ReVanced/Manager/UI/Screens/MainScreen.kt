package ru.Blays.ReVanced.Manager.UI.Screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.pullrefresh.pullRefresh
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import ru.Blays.ReVanced.Manager.Data.Apps
import ru.Blays.ReVanced.Manager.Repository.DownloadsRepository
import ru.Blays.ReVanced.Manager.Repository.SettingsRepository
import ru.Blays.ReVanced.Manager.UI.Screens.destinations.AppUpdateScreenDestination
import ru.Blays.ReVanced.Manager.UI.Screens.destinations.DownloadsScreenDestination
import ru.Blays.ReVanced.Manager.UI.Screens.destinations.VersionsListScreenDestination
import ru.Blays.ReVanced.Manager.UI.ViewModels.AppUpdateScreenViewModel
import ru.Blays.ReVanced.Manager.UI.ViewModels.MainScreenViewModel
import ru.blays.revanced.Elements.Elements.Screens.MainScreen.AppCard
import ru.blays.revanced.Elements.Elements.Screens.MainScreen.AppCardRoot
import ru.blays.revanced.Elements.GlobalState.NavBarState
import ru.blays.revanced.shared.R
import ru.blays.revanced.shared.Util.getStringRes
import ru.hh.toolbar.custom_toolbar.CollapsingTitle
import ru.hh.toolbar.custom_toolbar.CustomToolbar
import ru.hh.toolbar.custom_toolbar.rememberToolbarScrollBehavior

@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun MainScreen(
    viewModel: MainScreenViewModel = koinViewModel(),
    settingsRepository: SettingsRepository = koinInject(),
    downloadsRepository: DownloadsRepository = koinInject(),
    updateScreenViewModel: AppUpdateScreenViewModel = koinInject(),
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

    val lazyListState = rememberLazyListState()

    if (!lazyListState.canScrollForward && lazyListState.canScrollBackward) NavBarState.shouldHideNavigationBar = true
    else if (!lazyListState.canScrollForward && !lazyListState.canScrollBackward) NavBarState.shouldHideNavigationBar = false
    else NavBarState.shouldHideNavigationBar = false


    Scaffold(
        topBar = {
            CustomToolbar(
                collapsingTitle = CollapsingTitle.large(titleText = getStringRes(R.string.AppBar_Main)),
                scrollBehavior = scrollBehavior,
                actions = {
                    BadgedBox(
                        badge = {
                            if (updateScreenViewModel.isUpdateAvailable) Badge(
                                modifier = Modifier
                                    .offset(x = (-12).dp, y = 8.dp),
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = .8F)
                            ) {}
                        }
                    ) {
                        IconButton(onClick = { navController.navigate(AppUpdateScreenDestination) }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.round_update_24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = .8F)
                            )
                        }
                    }
                    BadgedBox(
                        badge = {
                            Badge(
                                modifier = Modifier
                                    .offset(x = (-12).dp, y = 8.dp),
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = .8F)
                            ) {
                                Text(text = downloadsRepository.downloadsCount.intValue.toString())
                            }
                        }
                    ) {
                        IconButton(onClick = { navController.navigate(DownloadsScreenDestination)}) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.round_download_24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = .8F)
                            )
                        }
                    }
                }
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
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                state = lazyListState
            ) {
                items(Apps.values()) { app ->
                    if (
                        (app == Apps.YOUTUBE && settingsRepository.youtubeManaged) ||
                        (app == Apps.YOUTUBE_MUSIC && settingsRepository.musicManaged) ||
                        (app == Apps.MICROG && settingsRepository.microGManaged)
                    ) {
                        if (app.repository.hasRootVersion) {
                            AppCardRoot(
                                icon = app.icon,
                                appName = app.repository.appName,
                                availableVersion = app.repository.availableVersion,
                                rootVersion = app.repository.rootVersion,
                                nonRootVersion = app.repository.nonRootVersion
                            ) {
                                navController.navigate(
                                    VersionsListScreenDestination(app.repository.appType)
                                )
                            }
                        } else {
                            AppCard(
                                icon = app.icon,
                                appName = app.repository.appName,
                                availableVersion = app.repository.availableVersion,
                                version = app.repository.version
                            ) {
                                navController.navigate(
                                    VersionsListScreenDestination(app.repository.appType)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
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