package ru.Blays.ReVanced.Manager.UI.Screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import org.koin.compose.koinInject
import ru.Blays.ReVanced.Manager.Repository.DownloadsRepository
import ru.blays.revanced.Elements.Elements.Screens.DownloadsScreen.DownloadItem
import ru.blays.revanced.Elements.Elements.Screens.DownloadsScreen.FileItem
import ru.blays.revanced.shared.R
import ru.blays.revanced.shared.Util.getStringRes
import ru.hh.toolbar.custom_toolbar.CollapsingTitle
import ru.hh.toolbar.custom_toolbar.CustomToolbar
import ru.hh.toolbar.custom_toolbar.rememberToolbarScrollBehavior

@Destination
@Composable
fun DownloadsScreen(
    repository: DownloadsRepository = koinInject(),
    navController: NavController
) {

    val scrollBehavior = rememberToolbarScrollBehavior()

    val list = repository.downloadsList

    val existingFilesList = repository.existingFilesList

    Scaffold(
        topBar = {
            CustomToolbar(
                collapsingTitle = CollapsingTitle.large(titleText = getStringRes(R.string.AppBar_Downloads)),
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
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                .fillMaxSize()
                .nestedScroll(connection = scrollBehavior.nestedScrollConnection)
        ) {
            items(list) {
                DownloadItem(downloadInfo = it, repository::removeFromList)
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(existingFilesList) {
                FileItem(file = it, actionRemove = repository::removeExistingFile)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        if (repository.downloadsList.isEmpty() && repository.existingFilesList.isEmpty()) {
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
                        imageVector = ImageVector.vectorResource(id = R.drawable.round_file_download_off_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = .8F)
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = stringResource(id = R.string.Downloads_not_found),
                        style = MaterialTheme.typography.displayMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}