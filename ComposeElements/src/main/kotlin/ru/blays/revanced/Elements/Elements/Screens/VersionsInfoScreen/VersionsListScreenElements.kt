package ru.blays.revanced.Elements.Elements.Screens.VersionsInfoScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.jeziellago.compose.markdowntext.MarkdownText
import ru.blays.revanced.Elements.DataClasses.AppInfo
import ru.blays.revanced.Elements.DataClasses.CardShape
import ru.blays.revanced.Elements.DataClasses.DefaultPadding
import ru.blays.revanced.Elements.DataClasses.RootVersionDownloadModel
import ru.blays.revanced.Elements.Elements.CustomButton.CustomIconButton
import ru.blays.revanced.Elements.Elements.FloatingBottomMenu.surfaceColorAtAlpha
import ru.blays.revanced.Services.Root.ModuleIntstaller.ModuleInstaller
import ru.blays.revanced.domain.DataClasses.ApkInfoModelDto
import ru.blays.revanced.domain.DataClasses.VersionsInfoModelDto
import ru.blays.revanced.shared.R
import java.time.Duration

@Composable
fun VersionsListScreenHeader(
    appInfo: AppInfo,
    actionOpenDialog: () -> Unit,
    actionOpen: (String) -> Unit
) {

    val version = appInfo.version
    val patchesVersion = appInfo.patchesVersion

    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .weight(.5F)
        ) {
            version?.let {
                Text(text = "${stringResource(R.string.Installed_version)}: $it")
            }
            patchesVersion?.let {
                Text(text = "${stringResource(R.string.Patches_version)}: $it")
            }
        }
        version?.let {
            CustomIconButton(
                onClick = actionOpenDialog,
                containerColor = MaterialTheme.colorScheme.surfaceColorAtAlpha(0.3F),
                contentColor = MaterialTheme.colorScheme.primary.copy(alpha = .8F)
            ) {
                Icon(imageVector = ImageVector.vectorResource(id = R.drawable.round_delete_24), contentDescription = "Delete app")
            }
            Spacer(modifier = Modifier.width(10.dp))
            CustomIconButton(
                onClick = {appInfo.packageName?.let { actionOpen(it) }},
                containerColor = MaterialTheme.colorScheme.surfaceColorAtAlpha(0.3F),
                contentColor = MaterialTheme.colorScheme.primary.copy(alpha = .8F)
            ) {
                Icon(imageVector = ImageVector.vectorResource(id = R.drawable.round_launch_24), contentDescription = "Launch app")
            }
            Spacer(modifier = Modifier.width(12.dp))
        }
    }
    Divider(
        modifier = Modifier.padding(top = 6.dp),
        thickness = 2.dp
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun DeleteConfirmDialogContent(
    appInfo: AppInfo,
    actionDelete: (String) -> Unit,
    actionHideDialog: () -> Unit
) {


    Column(modifier = Modifier
        .padding(DefaultPadding.CardDefaultPadding)
        .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.Action_uninstall_confirm),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = actionHideDialog
            ) {
                Text(text = stringResource(R.string.Action_Cancel))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    appInfo.packageName?.let { actionDelete(it) }
                    actionHideDialog()
                }
            ) {
                Text(text = stringResource(R.string.Action_OK))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VersionsInfoCard(
    item: VersionsInfoModelDto,
    actionShowChangelog: (String) -> Unit,
    actionShowApkList: (String, Boolean) -> Unit,
    rootVersions: Boolean
) {

    var isExpanded by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier
            .padding(DefaultPadding.CardDefaultPadding)
            .fillMaxWidth(),
        shape = CardShape.CardStandalone,
        onClick = {
            isExpanded = !isExpanded
        }
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            item.version?.let { Text(text = "${stringResource(R.string.Version)}: $it") }
            Spacer(modifier = Modifier.height(4.dp))
            item.patchesVersion?.let { Text(text = "${stringResource(R.string.Patches_version)}: $it") }
            Spacer(modifier = Modifier.height(4.dp))
            item.buildDate?.let { Text(text = "${stringResource(R.string.Build_date)}: $it") }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = slideInVertically(
                animationSpec = spring(stiffness = 300F, dampingRatio = .6F),
                initialOffsetY = { -it / 2 }
            ) + expandVertically(),
            exit = slideOutVertically(
                animationSpec = spring(stiffness = 300F, dampingRatio = .6F),
                targetOffsetY = { -it / 2 }
            ) + shrinkVertically()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceColorAtAlpha(0.1f),
                        shape = CardShape.CardStandalone
                    )
                    .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = {
                        item.changelogLink?.let {
                            actionShowChangelog(it)
                        }
                    }
                ) {
                    Text(text = stringResource(R.string.Action_changelog))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    onClick = {
                        actionShowApkList(item.versionsListLink.orEmpty(), rootVersions)
                    }
                ) {
                    Text(text = stringResource(R.string.Action_download))
                }
                Spacer(modifier = Modifier.width(12.dp))
            }
        }
    }
}


@Composable
fun SubversionsListBSContent(
    list: List<ApkInfoModelDto>,
    actionHide: () -> Unit,
    actionDownloadNonRootVersion: (String, String) -> Unit,
    actionDownloadRootVersion: (RootVersionDownloadModel) -> Unit,
    rootItemBackground: Color,
    nonRootItemBackground: Color
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(list) { item ->
            ApkListItem(
                item = item,
                hideBottomSheet = actionHide,
                actionDownloadNonRootVersion = actionDownloadNonRootVersion,
                actionDownloadRootVersion = actionDownloadRootVersion,
                rootItemBackground = rootItemBackground,
                nonRootItemBackground = nonRootItemBackground
            )
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
    Spacer(Modifier.fillMaxHeight(0.1F))
}

@Composable
fun ChangelogBSContent(markdown: String) {

    val scrollState = rememberScrollState()

    MarkdownText(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxSize()
            .verticalScroll(scrollState),
        markdown = markdown,
        color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(Modifier.fillMaxHeight(0.1F))
}

@Suppress("AnimatedContentLabel")
@Composable
fun ModuleInstallDialogContent(
    status: ModuleInstaller.Status,
    actionReboot: () -> Unit,
    actionHide: () -> Unit
) {
    when (status) {
        ModuleInstaller.Status.COMPLETE -> {
            Column(
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.Module_installer_complite),
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(id = R.string.Action_reboot_confirm))
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = actionHide
                    ) {
                        Text(text = stringResource(R.string.Action_Cancel))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = actionReboot
                    ) {
                        Text(text = stringResource(R.string.Action_OK))
                    }
                }
            }
        }
        ModuleInstaller.Status.ERROR -> {
            Column(
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.Module_installer_failed),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "${stringResource(id = R.string.Module_installer_error)}: ${status.error}")
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = actionHide
                    ) {
                        Text(text = stringResource(R.string.Action_OK))
                    }
                }
            }
        }
        else -> {
            Row(
                modifier = Modifier
                    .padding(
                        horizontal = 14.dp,
                        vertical = 16.dp
                    )
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(strokeCap = StrokeCap.Round)
                AnimatedContent(
                    targetState = status,
                    transitionSpec = {
                        (slideInVertically { height -> -height } + fadeIn())
                            .togetherWith(
                                slideOutVertically { height -> height } + fadeOut()
                            ).using(
                                SizeTransform(true)
                            )
                    }
                ) { status ->
                    Text(text = status.message)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable fun ApkListItem(
    item: ApkInfoModelDto,
    hideBottomSheet: () -> Unit,
    actionDownloadNonRootVersion: (String, String) -> Unit,
    actionDownloadRootVersion: (RootVersionDownloadModel) -> Unit,
    rootItemBackground: Color,
    nonRootItemBackground: Color,
) {

    Card(
        modifier = Modifier
            .padding(DefaultPadding.CardDefaultPadding),
        colors = CardDefaults.cardColors(containerColor = if (item.isRootVersion) rootItemBackground else nonRootItemBackground)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(.8F)
            ) {
                Text(
                    modifier = Modifier
                        .basicMarquee(
                            iterations = Int.MAX_VALUE,
                            spacing = MarqueeSpacing(24.dp)
                        ),
                    text = item.name,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${stringResource(R.string.Description)}: ${item.description}")
            }
            IconButton(
                onClick = {
                    if (item.isRootVersion && item.origApkUrl != null) {
                        actionDownloadRootVersion(
                           RootVersionDownloadModel(
                               fileName = item.name,
                               modUrl = item.url,
                               origUrl = item.origApkUrl
                           )
                        )
                    } else if (!item.isRootVersion) {
                        actionDownloadNonRootVersion(item.name, item.url)
                    }
                    hideBottomSheet()
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.round_download_24),
                    contentDescription = "DownloadButton",
                    modifier = Modifier.scale(1.3F)
                )
            }
        }
    }
}

private fun Duration.toMillisInt(): Int = this.toMillis().toInt()