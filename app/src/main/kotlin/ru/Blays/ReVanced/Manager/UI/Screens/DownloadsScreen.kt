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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import ru.Blays.ReVanced.Manager.Repository.DownloadsRepository
import ru.Blays.ReVanced.Manager.UI.Navigation.shouldHideNavigationBar
import ru.blays.helios.androidx.AndroidScreen
import ru.blays.helios.navigator.LocalNavigator
import ru.blays.helios.navigator.currentOrThrow
import ru.blays.revanced.Elements.Elements.Screens.DownloadsScreen.DownloadItem
import ru.blays.revanced.Elements.Elements.Screens.DownloadsScreen.FileItem
import ru.blays.revanced.shared.Extensions.open
import ru.blays.revanced.shared.R
import ru.hh.toolbar.custom_toolbar.CollapsingTitle
import ru.hh.toolbar.custom_toolbar.CustomToolbar
import ru.hh.toolbar.custom_toolbar.rememberToolbarScrollBehavior

class DownloadsScreen: AndroidScreen(){

    @Composable
    override fun Content() {

        val context = LocalContext.current

        val navigator = LocalNavigator.currentOrThrow

        val repository: DownloadsRepository = koinInject()

        val scrollBehavior = rememberToolbarScrollBehavior()

        val downloadingFilesList = repository.downloadsList

        val existingFilesList = repository.existingFilesList

        val existingDocumentsList = repository.existingDocumentsList

        val lazyListState = rememberLazyListState()

        shouldHideNavigationBar = when {
            !lazyListState.canScrollForward && lazyListState.canScrollBackward -> true
            !lazyListState.canScrollForward && !lazyListState.canScrollBackward -> false
            else -> false
        }

        Scaffold(
            topBar = {
                CustomToolbar(
                    collapsingTitle = CollapsingTitle.large(titleText = stringResource(R.string.AppBar_Downloads)),
                    scrollBehavior = scrollBehavior,
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
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .padding(top = padding.calculateTopPadding())
                    .fillMaxSize()
                    .nestedScroll(connection = scrollBehavior.nestedScrollConnection),
                state = lazyListState
            ) {
                items(downloadingFilesList) { item ->
                    DownloadItem(
                        fileName = item.fileName,
                        fileLength = item.file?.length()
                            ?: item.simpleDocument?.length
                            ?: 0L,
                        progressFlow = item.progressFlow,
                        speedFlow = item.speedFlow,
                        actionOpenFile = {
                            item.file?.open(context)
                            item.simpleDocument?.open()
                        },
                        actionDeleteFile = {
                            item.file?.delete()
                            item.simpleDocument?.delete()
                        },
                        actionRemove = {
                            repository.removeFromList(item)
                        },
                        actionPause = item.actionPauseResume
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(existingFilesList) {file ->
                    FileItem(
                        fileName = file.nameWithoutExtension,
                        fileLength = file.length(),
                        actionOpenFile = { file.open(context) },
                        actionDeleteFile = { repository.removeExistingFile(file) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(existingDocumentsList) {document ->
                    FileItem(
                        fileName = document.name ?: "",
                        fileLength = document.length ?: 0,
                        actionOpenFile = document::open,
                        actionDeleteFile = { repository.removeExistingFile(document) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (
                repository.downloadsList.isEmpty() &&
                repository.existingFilesList.isEmpty() &&
                repository.existingDocumentsList.isEmpty()
            ) {
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
}