package ru.blays.revanced.Presentation.Screens

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
import com.theapache64.rebugger.Rebugger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import ru.blays.revanced.Presentation.Elements.Screens.VersionsInfoScreen.VersionsInfoCard
import ru.blays.revanced.Presentation.ViewModels.VersionsListScreenViewModel
import ru.hh.toolbar.custom_toolbar.CollapsingTitle
import ru.hh.toolbar.custom_toolbar.CustomToolbar

@Destination
@Composable
fun VersionsListScreen(
    viewModel: VersionsListScreenViewModel = koinViewModel(),
    navController: NavController
) {

    val pullRefreshState = rememberPullRefreshState(
        refreshing = viewModel.isRefreshing,
        onRefresh = {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getList(VersionsListScreenViewModel.YOUTUBE)
            }
        }
    )

    val list = viewModel.list

    LaunchedEffect(Unit) {
        viewModel.getList(VersionsListScreenViewModel.YOUTUBE)
    }

    Scaffold(
        topBar = {
            CustomToolbar(
                collapsingTitle = CollapsingTitle.large(titleText = "Версии"),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "NavigateBack")
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
                items(list) { item ->
                    VersionsInfoCard(item = item)
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
    Rebugger(
        trackMap = mapOf(
            "viewModel" to viewModel,
            "pullRefreshState" to pullRefreshState,
            "list" to list,
        ),
    )
}