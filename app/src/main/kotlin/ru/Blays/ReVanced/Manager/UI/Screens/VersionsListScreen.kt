package ru.Blays.ReVanced.Manager.UI.Screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.pullrefresh.pullRefresh
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import ru.Blays.ReVanced.Manager.UI.Theme.cardBackgroundBlue
import ru.Blays.ReVanced.Manager.UI.Theme.cardBackgroundRed
import ru.Blays.ReVanced.Manager.UI.ViewModels.VersionsListScreenViewModel
import ru.blays.revanced.Elements.DataClasses.AppInfo
import ru.blays.revanced.Elements.Elements.Screens.VersionsInfoScreen.ChangelogBottomSheet
import ru.blays.revanced.Elements.Elements.Screens.VersionsInfoScreen.CustomTabIndicator
import ru.blays.revanced.Elements.Elements.Screens.VersionsInfoScreen.SubversionsListBottomSheet
import ru.blays.revanced.Elements.Elements.Screens.VersionsInfoScreen.VersionsInfoCard
import ru.blays.revanced.Elements.Elements.Screens.VersionsInfoScreen.VersionsListScreenHeader
import ru.blays.revanced.Elements.Util.getStringRes
import ru.blays.revanced.Presentation.R
import ru.hh.toolbar.custom_toolbar.CollapsingTitle
import ru.hh.toolbar.custom_toolbar.CustomToolbar

@OptIn(ExperimentalFoundationApi::class)
@Destination
@Composable
fun VersionsListScreen(
    appType: String,
    viewModel: VersionsListScreenViewModel = koinViewModel(),
    navController: NavController
) {

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = appType) {
        viewModel.getAppsEnumByAppType(appType)
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = viewModel.isRefreshing,
        onRefresh = {
            viewModel.coroutineScope.launch {
                viewModel.getList(appType)
            }
        }
    )

    val pagerState = rememberPagerState {
        viewModel.pagesCount
    }

    val currentPage = pagerState.currentPage
    val settledPage = pagerState.settledPage
    val rootVersionsPage = settledPage == 1

    Scaffold(
        topBar = {
            CustomToolbar(
                collapsingTitle = CollapsingTitle.large(titleText = getStringRes(R.string.AppBar_Versions)),
                navigationIcon = {
                    IconButton(
                        onClick = navController::navigateUp
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "NavigateBack"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .pullRefresh(state = pullRefreshState)
                .padding(padding)
                .fillMaxSize()
        ) {

            Column {

                if (viewModel.pagesCount > 1) {
                    TabRow(
                        modifier = Modifier
                            .fillMaxWidth(),
                        selectedTabIndex = currentPage,
                        indicator = {
                            CustomTabIndicator(
                                currentPagePosition = it[currentPage],
                                height = 4.dp
                            )
                        },
                        divider = {}
                    ) {
                        Tab(
                            selected = currentPage == 0,
                            onClick = { scope.launch { pagerState.animateScrollToPage(0) } }
                        ) {
                            Text(text = "Non-Root")
                            Spacer(Modifier.height(12.dp))
                        }

                        Tab(
                            selected = currentPage == 1,
                            onClick = { scope.launch { pagerState.animateScrollToPage(1) } }
                        ) {
                            Text(text = "Root")
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }

                HorizontalPager(state = pagerState) { page ->

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {

                        if (page == 0) {
                            stickyHeader {
                                VersionsListScreenHeader(
                                    appInfo = viewModel.repository?.generateAppInfo() ?: AppInfo(),
                                    actionDelete = viewModel::delete,
                                    actionOpen = viewModel::launch
                                )
                            }
                        } else if (page == 1) {
                            stickyHeader {
                                VersionsListScreenHeader(
                                    appInfo = viewModel.repository?.generateAppInfo(true) ?: AppInfo(),
                                    actionDelete = viewModel::delete,
                                    actionOpen = viewModel::launch
                                )
                            }
                        }

                        items(viewModel.list) { item ->
                            VersionsInfoCard(
                                item = item,
                                actionShowChangelog = viewModel::showChangelogBottomSheet,
                                actionShowApkList = viewModel::showApkListBottomSheet
                            )
                        }
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = viewModel.isRefreshing,
                state = pullRefreshState,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.TopCenter)
            )
        }
    }

    SubversionsListBottomSheet(
        rootVersionsPage = rootVersionsPage,
        isExpanded = viewModel.isApkListBottomSheetExpanded,
        list = viewModel.bottomSheetList,
        actionDownloadNonRootVersion = viewModel::downloadNonRootVersion,
        actionDownloadRootVersion = viewModel::downloadRootVersion,
        rootItemBackground = cardBackgroundRed,
        nonRootItemBackground = cardBackgroundBlue
    )

    ChangelogBottomSheet(
        isExpanded = viewModel.isChangelogBottomSheetExpanded,
        changelog = viewModel.changelog
    )
}