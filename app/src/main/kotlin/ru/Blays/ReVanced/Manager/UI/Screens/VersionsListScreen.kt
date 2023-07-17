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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import ru.Blays.ReVanced.Manager.Data.Apps
import ru.Blays.ReVanced.Manager.Repository.DownloadsRepository
import ru.Blays.ReVanced.Manager.UI.Navigation.shouldHideNavigationBar
import ru.Blays.ReVanced.Manager.UI.Theme.cardBackgroundBlue
import ru.Blays.ReVanced.Manager.UI.Theme.cardBackgroundRed
import ru.Blays.ReVanced.Manager.UI.ViewModels.VersionsListScreenViewModel
import ru.Blays.ReVanced.Manager.Utils.showSuspend
import ru.blays.helios.androidx.AndroidScreen
import ru.blays.helios.core.Screen
import ru.blays.helios.navigator.LocalNavigator
import ru.blays.helios.navigator.bottomSheet.LocalBottomSheetNavigator
import ru.blays.helios.navigator.currentOrThrow
import ru.blays.revanced.Elements.DataClasses.AppInfo
import ru.blays.revanced.Elements.DataClasses.RootVersionDownloadModel
import ru.blays.revanced.Elements.Elements.CustomTabs.CustomTab
import ru.blays.revanced.Elements.Elements.CustomTabs.CustomTabIndicator
import ru.blays.revanced.Elements.Elements.CustomTabs.CustomTabRow
import ru.blays.revanced.Elements.Elements.Screens.VersionsInfoScreen.ChangelogBSContent
import ru.blays.revanced.Elements.Elements.Screens.VersionsInfoScreen.MagiskInstallInfoDialog
import ru.blays.revanced.Elements.Elements.Screens.VersionsInfoScreen.SubversionsListBSContent
import ru.blays.revanced.Elements.Elements.Screens.VersionsInfoScreen.VersionsInfoCard
import ru.blays.revanced.Elements.Elements.Screens.VersionsInfoScreen.VersionsListScreenHeader
import ru.blays.revanced.domain.DataClasses.ApkInfoModelDto
import ru.blays.revanced.shared.LogManager.BLog
import ru.blays.revanced.shared.R
import ru.hh.toolbar.custom_toolbar.CollapsingTitle
import ru.hh.toolbar.custom_toolbar.CustomToolbar
import ru.hh.toolbar.custom_toolbar.rememberToolbarScrollBehavior

private const val TAG = "VersionsInfoScreen"

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
class VersionsListScreen(private val appType: Apps): AndroidScreen() {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val bottomSheetNavigator = LocalBottomSheetNavigator.current

        val viewModel: VersionsListScreenViewModel = koinViewModel()
        val downloadsRepository: DownloadsRepository = koinInject()

        // Coroutine scope for launch suspend functions
        val scope = rememberCoroutineScope()

        // Get info about app on screen launch
        LaunchedEffect(key1 = appType) {
            viewModel.getDataForApp(appType)
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
                            onClick = navigator::pop
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
                            IconButton(onClick = { navigator.push(DownloadsScreen()) }) {
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

                // Hide content while data refreshing
                if (!viewModel.isRefreshing) {
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
                                        actionShowChangelog = { url ->
                                            bottomSheetNavigator.showSuspend(scope = viewModel) {
                                                ChangelogBS(
                                                    markdown = viewModel.getChangelog(url)
                                                )
                                            }
                                        },
                                        actionShowApkList = { url, isRoot ->
                                            bottomSheetNavigator.showSuspend(scope = viewModel) {
                                                VersionsListBS(
                                                    list = viewModel.getApkList(url, isRoot),
                                                    actionDownloadRootVersion = viewModel::downloadRootVersion,
                                                    actionDownloadNonRootVersion = viewModel::downloadNonRootVersion
                                                )
                                            }
                                        },
                                        rootVersions = rootVersionsPage
                                    )
                                }
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

        if (viewModel.magiskInstallerDialogState.isExpanded) {
            BLog.i(TAG, "Open reboot dialog")
            MagiskInstallInfoDialog(
                state = viewModel.magiskInstallerDialogState,
                actionReboot = viewModel::reboot,
                actionHide = viewModel.hideRebootAlertDialog
            )
        }
    }
}

private class VersionsListBS(
    private val list: List<ApkInfoModelDto>,
    private val actionDownloadNonRootVersion: (String, String) -> Unit,
    private val actionDownloadRootVersion: (RootVersionDownloadModel) -> Unit,
    private val rootItemBackground: Color = cardBackgroundRed,
    private val nonRootItemBackground: Color = cardBackgroundBlue
): Screen {
    @Composable
    override fun Content() {
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        SubversionsListBSContent(
            list = list,
            actionHide = bottomSheetNavigator::hide,
            actionDownloadNonRootVersion = actionDownloadNonRootVersion,
            actionDownloadRootVersion = actionDownloadRootVersion,
            rootItemBackground = rootItemBackground,
            nonRootItemBackground = nonRootItemBackground
        )
    }
}

private class ChangelogBS(private val markdown: String): Screen {
    @Composable
    override fun Content() {
        ChangelogBSContent(markdown = markdown)
    }
}