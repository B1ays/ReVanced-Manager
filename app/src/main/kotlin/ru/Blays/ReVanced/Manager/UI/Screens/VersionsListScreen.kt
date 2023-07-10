package ru.Blays.ReVanced.Manager.UI.Screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import ru.Blays.ReVanced.Manager.Repository.DownloadsRepository
import ru.Blays.ReVanced.Manager.UI.Navigation.shouldHideNavigationBar
import ru.Blays.ReVanced.Manager.UI.Screens.destinations.DownloadsScreenDestination
import ru.Blays.ReVanced.Manager.UI.Theme.cardBackgroundBlue
import ru.Blays.ReVanced.Manager.UI.Theme.cardBackgroundRed
import ru.Blays.ReVanced.Manager.UI.ViewModels.VersionsListScreenViewModel
import ru.blays.revanced.Elements.DataClasses.AppInfo
import ru.blays.revanced.Elements.Elements.CustomTabs.CustomTab
import ru.blays.revanced.Elements.Elements.CustomTabs.CustomTabIndicator
import ru.blays.revanced.Elements.Elements.CustomTabs.CustomTabRow
import ru.blays.revanced.Elements.Elements.Screens.VersionsInfoScreen.ChangelogBottomSheet
import ru.blays.revanced.Elements.Elements.Screens.VersionsInfoScreen.MagiskInstallInfoDialog
import ru.blays.revanced.Elements.Elements.Screens.VersionsInfoScreen.SubversionsListBottomSheet
import ru.blays.revanced.Elements.Elements.Screens.VersionsInfoScreen.VersionsInfoCard
import ru.blays.revanced.Elements.Elements.Screens.VersionsInfoScreen.VersionsListScreenHeader
import ru.blays.revanced.shared.LogManager.BLog
import ru.blays.revanced.shared.R
import ru.hh.toolbar.custom_toolbar.CollapsingTitle
import ru.hh.toolbar.custom_toolbar.CustomToolbar
import ru.hh.toolbar.custom_toolbar.rememberToolbarScrollBehavior

private const val TAG = "VersionsInfoScreen"

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Destination
@Composable
fun VersionsListScreen(
    appType: String,
    viewModel: VersionsListScreenViewModel = koinViewModel(),
    downloadsRepository: DownloadsRepository = koinInject(),
    navController: NavController
) {

    // Coroutine scope for launch suspend functions
    val scope = rememberCoroutineScope()

    // Get info about app on screen launch
    LaunchedEffect(key1 = appType) {
        viewModel.getAppsEnumByAppType(appType)
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = viewModel.isRefreshing,
        onRefresh = viewModel::onRefresh
    )

    // AppBar scroll behavior
    val scrollBehavior = rememberToolbarScrollBehavior()

    val pagerState = rememberPagerState {
        viewModel.pagesCount
    }

    // page number with real time update
    val currentPage = pagerState.currentPage

    // page number of static page
    val settledPage = pagerState.settledPage

    // Is the current page a page with root versions
    val rootVersionsPage = settledPage == 1

    // Lazy list state
    val lazyListState = rememberLazyListState()

    shouldHideNavigationBar = when {
        !lazyListState.canScrollForward && lazyListState.canScrollBackward -> true
        !lazyListState.canScrollForward && !lazyListState.canScrollBackward -> false
        else -> false
    }

    Scaffold(
        topBar = {
            CustomToolbar(
                collapsingTitle = CollapsingTitle.large(
                    titleText = viewModel.appName
                ),
                navigationIcon = {
                    IconButton(
                        onClick = navController::navigateUp
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = .8F)
                        )
                    }
                },
                actions = {
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
                },
                scrollBehavior = scrollBehavior
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

                // If pages > 1 then show tabs
                if (viewModel.pagesCount > 1) {
                    CustomTabRow(
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        selectedTabIndex = currentPage,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape,
                        indicator = {
                            CustomTabIndicator(
                                currentPagePosition = it[currentPage],
                                shape = CircleShape,
                                padding = 4.dp
                            )
                        }
                    ) {
                        CustomTab(
                            selected = currentPage == 0,
                            selectedContentColor = MaterialTheme.colorScheme.surface,
                            unselectedContentColor = MaterialTheme.colorScheme.primary,
                            minHeight = 45.dp,
                            onClick = { scope.launch { pagerState.animateScrollToPage(0) } }
                        ) {
                            Text(text = "Non-Root")
                        }

                        CustomTab(
                            selected = currentPage == 1,
                            selectedContentColor = MaterialTheme.colorScheme.surface,
                            unselectedContentColor = MaterialTheme.colorScheme.primary,
                            minHeight = 45.dp,
                            onClick = { scope.launch { pagerState.animateScrollToPage(1) } }
                        ) {
                            Text(text = "Root")
                        }
                    }
                }

                HorizontalPager(state = pagerState) { page ->

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                        state = lazyListState
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
                                    actionDelete = viewModel::deleteModule,
                                    actionOpen = viewModel::launch
                                )
                            }
                        }

                        items(viewModel.list) { item ->
                            VersionsInfoCard(
                                item = item,
                                actionShowChangelog = viewModel::showChangelogBottomSheet,
                                actionShowApkList = viewModel::showApkListBottomSheet,
                                rootVersions = rootVersionsPage
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

    if (viewModel.magiskInstallerDialogState.isExpanded) {
        BLog.i(TAG, "Open reboot dialog")
        MagiskInstallInfoDialog(
            state = viewModel.magiskInstallerDialogState,
            actionReboot = viewModel::reboot,
            actionHide = viewModel.hideRebootAlertDialog
        )
    }
}