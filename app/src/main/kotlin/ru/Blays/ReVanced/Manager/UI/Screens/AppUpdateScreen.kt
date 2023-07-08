package ru.Blays.ReVanced.Manager.UI.Screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import ru.Blays.ReVanced.Manager.Repository.DownloadsRepository
import ru.Blays.ReVanced.Manager.UI.Screens.destinations.DownloadsScreenDestination
import ru.Blays.ReVanced.Manager.UI.ViewModels.AppUpdateScreenViewModel
import ru.blays.revanced.Elements.Elements.Screens.AppUpdateScreen.ChangelogView
import ru.blays.revanced.Elements.Elements.Screens.AppUpdateScreen.UpdateInfoHeader
import ru.blays.revanced.shared.R
import ru.blays.revanced.shared.Util.getStringRes
import ru.hh.toolbar.custom_toolbar.CollapsingTitle
import ru.hh.toolbar.custom_toolbar.CustomToolbar
import ru.hh.toolbar.custom_toolbar.rememberToolbarScrollBehavior

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun AppUpdateScreen(
    viewModel: AppUpdateScreenViewModel = koinViewModel(),
    downloadsRepository: DownloadsRepository = koinInject(),
    navController: NavController
) {

    val model by viewModel.updateInfo.collectAsState()

    val scrollBehavior = rememberToolbarScrollBehavior()

    Scaffold(
        topBar = {
            CustomToolbar(
                collapsingTitle = CollapsingTitle.large(titleText = getStringRes(R.string.AppBar_Updates)),
                scrollBehavior = scrollBehavior,
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
                }
            )
        }
    ) { padding ->
        if (model != null) {
            Column(
                modifier = Modifier
                    .padding(top = padding.calculateTopPadding())
                    .fillMaxSize()
                    .nestedScroll(connection = scrollBehavior.nestedScrollConnection)
            ) {
                UpdateInfoHeader(
                    availableVersion = model?.availableVersion,
                    versionCode = model?.versionCode,
                    buildDate = model?.buildDate,
                    actionDownload = viewModel::downloadAndInstall
                )
                if (viewModel.changelog.isNotEmpty()) {
                    ChangelogView(changelog = viewModel.changelog)
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.7F),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        modifier = Modifier
                            .size(200.dp),
                        imageVector = ImageVector.vectorResource(id = R.drawable.round_update_disabled_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = .8F)
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = stringResource(id = R.string.Updates_not_found),
                        style = MaterialTheme.typography.displayMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}