package ru.Blays.ReVanced.Manager.UI.Screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Button
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import ru.Blays.ReVanced.Manager.Data.Apps
import ru.Blays.ReVanced.Manager.Repository.DownloadsRepository
import ru.Blays.ReVanced.Manager.UI.ComponentCallback.ComponentCallback
import ru.Blays.ReVanced.Manager.UI.ComponentCallback.IComponentCallback
import ru.Blays.ReVanced.Manager.UI.Navigation.shouldHideNavigationBar
import ru.Blays.ReVanced.Manager.UI.Theme.cardBackgroundBlue
import ru.Blays.ReVanced.Manager.UI.Theme.cardBackgroundRed
import ru.Blays.ReVanced.Manager.UI.ViewModels.VersionsListScreenViewModel
import ru.blays.helios.androidx.AndroidScreen
import ru.blays.helios.core.Screen
import ru.blays.helios.dialogs.LocalDialogNavigator
import ru.blays.helios.navigator.LocalNavigator
import ru.blays.helios.navigator.bottomSheet.LocalBottomSheetNavigator
import ru.blays.helios.navigator.bottomSheet.showSuspend
import ru.blays.helios.navigator.currentOrThrow
import ru.blays.revanced.DeviceUtils.Root.ModuleIntstaller.ModuleInstaller
import ru.blays.revanced.Elements.DataClasses.AppInfo
import ru.blays.revanced.Elements.DataClasses.DefaultPadding
import ru.blays.revanced.Elements.DataClasses.RootVersionDownloadModel
import ru.blays.revanced.Elements.Elements.CustomTabs.CustomTab
import ru.blays.revanced.Elements.Elements.CustomTabs.CustomTabIndicator
import ru.blays.revanced.Elements.Elements.CustomTabs.CustomTabRow
import ru.blays.revanced.Elements.Elements.Screens.VersionsInfoScreen.ChangelogBSContent
import ru.blays.revanced.Elements.Elements.Screens.VersionsInfoScreen.DeleteConfirmDialogContent
import ru.blays.revanced.Elements.Elements.Screens.VersionsInfoScreen.ModuleInstallDialogContent
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
        val dialogNavigator = LocalDialogNavigator.current

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

        // Lazy versionsList state
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
                                viewModel.repository?.appVersions?.forEachIndexed { index, version ->
                                    CustomTab(
                                        selected = currentPage == index,
                                        selectedContentColor = MaterialTheme.colorScheme.surface,
                                        unselectedContentColor = MaterialTheme.colorScheme.primary,
                                        minHeight = 45.dp,
                                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } }
                                    ) {
                                        Text(text = version.versionName ?: "")
                                    }
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
                                if (
                                    viewModel.repository
                                    ?.appVersions
                                    ?.get(page)
                                    ?.isRootNeeded == true
                                ) {
                                    stickyHeader {
                                        VersionsListScreenHeader(
                                            appInfo = viewModel.repository
                                                ?.createAppInfo(viewModel.repository
                                                !!.appVersions[page])
                                                ?: AppInfo(),
                                            actionOpenDialog = {
                                                dialogNavigator.show(
                                                    DeleteConfirmDialog(
                                                        appInfo = viewModel.repository
                                                            ?.createAppInfo(
                                                                viewModel.repository
                                                                !!.appVersions[page]
                                                            )
                                                            ?: AppInfo(),
                                                        actionDelete = viewModel::deleteModule
                                                    )
                                                )
                                            },
                                            actionOpen = viewModel::launch
                                        )
                                    }
                                } else {
                                    stickyHeader {
                                        VersionsListScreenHeader(
                                            appInfo = viewModel.repository
                                                ?.createAppInfo(
                                                    viewModel.repository
                                                    !!.appVersions[page]
                                                )
                                                ?: AppInfo(),
                                            actionOpenDialog = {
                                                dialogNavigator.show(
                                                    DeleteConfirmDialog(
                                                        appInfo = viewModel.repository
                                                            ?.createAppInfo(
                                                                viewModel.repository
                                                                !!.appVersions[page]
                                                            )
                                                            ?:AppInfo(),
                                                        actionDelete = viewModel::delete
                                                    )
                                                )
                                            },
                                            actionOpen = viewModel::launch
                                        )
                                    }
                                }

                                items(viewModel.versionsList) { item ->
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
                                                    actionDownloadRootVersion = { downloadModel ->
                                                        val callback =
                                                            ComponentCallback.builder<(MutableStateFlow<ModuleInstaller.Status>) -> Unit> { statusFlow ->
                                                                dialogNavigator.showNonDismissible(
                                                                    ModuleInstallerDialog(
                                                                        statusFlow,
                                                                        viewModel::reboot
                                                                    )
                                                                )
                                                            }
                                                        viewModel.downloadRootVersion(downloadModel, callback)
                                                    },
                                                    actionDownloadNonRootVersion = { fileName, url ->
                                                        val callback: IComponentCallback<() -> Unit> = ComponentCallback.builder {
                                                            onError = {
                                                                BLog.d(TAG, "install failed")
                                                                dialogNavigator.show(AppInstallResultDialog(fileName, false))
                                                            }
                                                            onSuccess = {
                                                                BLog.d(TAG, "install success")
                                                                dialogNavigator.show(AppInstallResultDialog(fileName, true))
                                                            }
                                                        }
                                                        viewModel.downloadNonRootVersion(fileName, url, callback)
                                                    }
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
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
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

class DeleteConfirmDialog(
    private val appInfo: AppInfo,
    private val actionDelete: (String) -> Unit
): Screen {
    @Composable
    override fun Content() {
        val dialogNavigator = LocalDialogNavigator.current
        DeleteConfirmDialogContent(
            appInfo = appInfo,
            actionDelete = actionDelete,
            actionHideDialog = dialogNavigator::hide
        )
    }
}

class ModuleInstallerDialog(
    private val statusFlow: MutableStateFlow<ModuleInstaller.Status>,
    private val actionReboot: () -> Unit
): Screen {
    @Composable
    override fun Content() {
        val status by statusFlow.collectAsState()
        val dialogNavigator = LocalDialogNavigator.current
        ModuleInstallDialogContent(
            status = status,
            actionReboot = actionReboot,
            actionHide = dialogNavigator::hide
        )
    }
}

class AppInstallResultDialog(
    private val fileName: String,
    private val isSuccess: Boolean
): Screen {
    @Composable
    override fun Content() {

        val dialogNavigator = LocalDialogNavigator.current

        Column(modifier = Modifier
            .padding(DefaultPadding.CardDefaultPadding)
            .fillMaxWidth()
        ) {
            Text(
                text = if (isSuccess) stringResource(R.string.App_install_success)
                else  stringResource(R.string.App_install_error),
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(7.dp))
            Text(
                text = stringResource(R.string.App_installed_apk) + ": $fileName"
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = dialogNavigator::hide
                ) {
                    Text(text = stringResource(R.string.Action_OK))
                }
            }
        }
    }
}