package ru.Blays.ReVanced.Manager.UI.Screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.pullrefresh.pullRefresh
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import ru.Blays.ReVanced.Manager.UI.Theme.cardBackgroundBlue
import ru.Blays.ReVanced.Manager.UI.Theme.cardBackgroundRed
import ru.Blays.ReVanced.Manager.UI.ViewModels.VersionsListScreenViewModel
import ru.blays.revanced.Elements.Elements.Screens.VersionsInfoScreen.ChangelogBottomSheet
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

    Scaffold(
        topBar = {
            CustomToolbar(
                collapsingTitle = CollapsingTitle.large(titleText = getStringRes(R.string.AppBar_Versions)),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                stickyHeader { VersionsListScreenHeader(appInfo = viewModel.appInfo, actionDelete = viewModel::delete, actionOpen = viewModel::launch) }
                items(viewModel.list) { item ->
                    VersionsInfoCard(item = item, actionShowChangelog = viewModel::showChangelogBottomSheet, actionShowApkList = viewModel::showApkListBottomSheet)
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

    SubversionsListBottomSheet(
        isExpanded = viewModel.isApkListBottomSheetExpanded,
        list = viewModel.bottomSheetList,
        actionDownloadFile = viewModel::downloadApk,
        rootItemBackground = cardBackgroundRed,
        nonRootItemBackground = cardBackgroundBlue
    )

    ChangelogBottomSheet(
        isExpanded = viewModel.isChangelogBottomSheetExpanded,
        changelog = viewModel.changelog
    )
}