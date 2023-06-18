package ru.blays.revanced.Elements.Elements.Screens.VersionsInfoScreen


import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.blays.revanced.Elements.DataClasses.AppInfo
import ru.blays.revanced.Elements.DataClasses.DefaultPadding
import ru.blays.revanced.Elements.DataClasses.RootVersionDownloadModel
import ru.blays.revanced.Elements.Elements.CustomButton.CustomIconButton
import ru.blays.revanced.Elements.Elements.FloatingBottomMenu.surfaceColorAtAlpha
import ru.blays.revanced.Elements.Elements.GradientProgressIndicator.GradientLinearProgressIndicator
import ru.blays.revanced.shared.Util.getStringRes
import ru.blays.revanced.shared.R
import ru.blays.revanced.data.Downloader.DataClass.DownloadInfo
import ru.blays.revanced.domain.DataClasses.ApkInfoModelDto
import ru.blays.revanced.domain.DataClasses.VersionsInfoModelDto
import java.time.Duration
import kotlin.reflect.KSuspendFunction1
import kotlin.reflect.KSuspendFunction2

@Composable
fun VersionsListScreenHeader(
    appInfo: AppInfo,
    actionDelete: (String) -> Unit,
    actionOpen: (String) -> Unit
) {
    
    var isAlertDialogShown by remember {
        mutableStateOf(false)
    } 
    
    val hideAlertDialog = { isAlertDialogShown = false }
    val showAlertDialog = { isAlertDialogShown = true }
    
    Column(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = appInfo.appName ?: "",
            style = MaterialTheme.typography.titleMedium
        )
        appInfo.version.collectAsState().value?.let {
            Text(text = "${getStringRes(R.string.Installed_version)}: $it")
        }
        appInfo.patchesVersion.collectAsState().value?.let {
            Text(text = "${getStringRes(R.string.Patches_version)}: $it")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            appInfo.version.collectAsState().value?.let {
                OutlinedButton(
                    onClick = showAlertDialog
                ) {
                    Text(text = getStringRes(R.string.Action_uninstall))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    onClick = {
                        appInfo.packageName?.let { actionOpen(it) }
                    }
                ) {
                    Text(text = getStringRes(R.string.Action_launch))
                }
            }
        }
        Divider(
            modifier = Modifier.padding(top = 6.dp),
            thickness = 2.dp)
    }

    if (isAlertDialogShown) AlertDialog(
        onDismissRequest = hideAlertDialog,
        title = {
            Text(text = getStringRes(R.string.Action_uninstall_confirm))
        },
        confirmButton = {
            Button(
                onClick = {
                    appInfo.packageName?.let { actionDelete(it) }
                    hideAlertDialog()
                }
            ) {
                Text(text = getStringRes(R.string.Action_OK))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = hideAlertDialog
            ) {
                Text(text = getStringRes(R.string.Action_Cancel))
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VersionsInfoCard(
    item: VersionsInfoModelDto,
    actionShowChangelog: KSuspendFunction1<String, Unit>,
    actionShowApkList: KSuspendFunction2<String, Boolean, Unit>,
    rootVersions: Boolean
) {

    var isExpanded by remember {
        mutableStateOf(false)
    }

    var isExist by remember {
        mutableStateOf(false)
    }

    val localDensity = LocalDensity.current

    var mainCardHeight by remember {
        mutableStateOf(0.dp)
    }

    var slidedCardHeight by remember {
        mutableStateOf(0.dp)
    }

    val animationDuration = Duration.ofMillis(500).toMillisInt()

    val offset by animateDpAsState(
        targetValue = if (isExpanded) mainCardHeight else 0.dp,
        animationSpec = tween(durationMillis = animationDuration), label = "slidingCardOffset"
    ) {
        if (!isExpanded) isExist = false
    }

    val bottomOffset by animateDpAsState(
        targetValue = if (isExpanded) slidedCardHeight else 0.dp,
        animationSpec = tween(durationMillis = animationDuration), label = "nextCardOffset"
    )

    val animatedCorners by animateDpAsState(
        targetValue = if (isExpanded) 0.dp else 12.dp,
        animationSpec = tween(durationMillis = animationDuration), label = "cornerAnimation"
    )

    Box(
        modifier = Modifier.padding(bottom = bottomOffset)
    ) {

        if (isExist) {
            Card(
                modifier = Modifier
                    .padding(
                        vertical = DefaultPadding.CardVerticalPadding,
                        horizontal = DefaultPadding.CardHorizontalPadding
                    )
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        slidedCardHeight = with(localDensity) { it.size.height.toDp() }
                    }
                    .offset(y = offset)
                    .clip(
                        RoundedCornerShape(
                            topStart = animatedCorners,
                            bottomStart = 12.dp,
                            bottomEnd = 12.dp,
                            topEnd = animatedCorners
                        )
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtAlpha(0.1f)
                ),
                shape = RectangleShape
            ) {

                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                item.changelogLink?.let {
                                    actionShowChangelog(it)
                                }
                            }
                        }
                    ) {
                        Text(text = getStringRes(R.string.Action_changelog))
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                actionShowApkList(item.versionsListLink.orEmpty(), rootVersions)
                            }
                        }
                    ) {
                        Text(text = getStringRes(R.string.Action_download))
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .padding(
                    vertical = DefaultPadding.CardVerticalPadding,
                    horizontal = DefaultPadding.CardHorizontalPadding
                )
                .fillMaxWidth()
                .onGloballyPositioned {
                    mainCardHeight = with(localDensity) { it.size.height.toDp() }
                }
                .clip(
                    shape = RoundedCornerShape(
                        topEnd = 12.dp,
                        topStart = 12.dp,
                        bottomEnd = animatedCorners,
                        bottomStart = animatedCorners
                    )
                ),
            shape = RectangleShape,
            onClick = {
                isExpanded = !isExpanded
                isExist = true
            }
        ) {

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                item.version?.let { Text(text = "${getStringRes(R.string.Version)}: $it") }
                Spacer(modifier = Modifier.height(4.dp))
                item.patchesVersion?.let { Text(text = "${getStringRes(R.string.Patches_version)}: $it") }
                Spacer(modifier = Modifier.height(4.dp))
                item.buildDate?.let { Text(text = "${getStringRes(R.string.Build_date)}: $it") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubversionsListBottomSheet(
    isExpanded: MutableStateFlow<Boolean>,
    list: MutableStateFlow<List<ApkInfoModelDto>>,
    actionDownloadNonRootVersion: (String, String) -> Unit,
    actionDownloadRootVersion: (RootVersionDownloadModel) -> Unit,
    rootItemBackground: Color,
    nonRootItemBackground: Color
) {

    val state = rememberModalBottomSheetState()

    LaunchedEffect(key1 = Unit) {
        state.expand()
    }

    val listState = list.collectAsState().value

    val hideBottomSheet = {
        isExpanded.tryEmit(false)
    }

    if (isExpanded.collectAsState().value) {
        ModalBottomSheet(
            onDismissRequest = { hideBottomSheet() },
            sheetState = state,

            ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(listState) { item ->
                    ApkListItem(
                        item = item,
                        actionDownloadNonRootVersion = actionDownloadNonRootVersion,
                        actionDownloadRootVersion = actionDownloadRootVersion,
                        hideBottomSheet = hideBottomSheet,
                        rootItemBackground = rootItemBackground,
                        nonRootItemBackground = nonRootItemBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
            Spacer(Modifier.fillMaxHeight(0.1F))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangelogBottomSheet(isExpanded: MutableStateFlow<Boolean>, changelog: MutableStateFlow<String>) {

    val state = rememberModalBottomSheetState()

    val text by changelog.collectAsState()

    val hideBottomSheet = {
        isExpanded.tryEmit(false)
    }

    if (isExpanded.collectAsState().value) {
        ModalBottomSheet(
            onDismissRequest = { hideBottomSheet() },
            sheetState = state
        ) {
            MarkdownText(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxSize(),
                markdown = text,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.fillMaxHeight(0.1F))
        }
    }
}

@Composable
fun RebootAlertDialog(actionReboot: () -> Unit, actionHide: () -> Unit) {
    AlertDialog(
        onDismissRequest = actionHide,
        title = {
            Text(text = getStringRes(R.string.Action_reboot_confirm))
        },
        confirmButton = {
            Button(
                onClick = actionReboot
            ) {
                Text(text = getStringRes(R.string.Action_OK))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = actionHide
            ) {
                Text(text = getStringRes(R.string.Action_Cancel))
            }
        }
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable fun ApkListItem(
    item: ApkInfoModelDto,
    hideBottomSheet: () -> Boolean,
    actionDownloadNonRootVersion: (String, String) -> Unit,
    actionDownloadRootVersion: (RootVersionDownloadModel) -> Unit,
    rootItemBackground: Color,
    nonRootItemBackground: Color,
) {

    Card(
        modifier = Modifier
            .padding(
                horizontal = DefaultPadding.CardHorizontalPadding,
                vertical = DefaultPadding.CardVerticalPadding
            ),
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
                Text(text = "${getStringRes(R.string.Description)}: ${item.description}")
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DownloadProgressContent(downloadStateList: SnapshotStateList<DownloadInfo>) {

    var isPaused by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        items(downloadStateList) { state ->

            val progress by state.progressFlow.collectAsState()

            val speed by state.speedFlow.collectAsState()

            val fileName = state.fileName

            Text(text = "${getStringRes(R.string.Download)}: $fileName")
            Spacer(modifier = Modifier.height(10.dp))
            GradientLinearProgressIndicator(
                progress = progress,
                strokeCap = StrokeCap.Round,
                brush = Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${stringResource(R.string.Progress)}: ${(progress * 100F).toInt()}%"
            )
            Text(
                text = "${stringResource(R.string.Speed)}: $speed ${stringResource(R.string.Speed_kbs)}"
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        stickyHeader {
            Row {
                CustomIconButton(
                    onClick = {
                        isPaused = !isPaused
                        downloadStateList.forEach {
                            it.actionPauseResume()
                        }
                    },
                    shape = MaterialTheme.shapes.medium,
                    contentPadding = PaddingValues(6.dp)
                ) {
                    Icon(
                        imageVector = if (isPaused) ImageVector.vectorResource(id = R.drawable.round_play_arrow_24) else
                            ImageVector.vectorResource(id = R.drawable.round_pause_24),
                        contentDescription = null,
                        modifier = Modifier.scale(1.3F)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                CustomIconButton(
                    onClick = {
                        downloadStateList.forEach {
                            it.actionCancel()
                        }
                    },
                    shape = MaterialTheme.shapes.medium,
                    contentPadding = PaddingValues(6.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.round_close_24),
                        contentDescription = null,
                        modifier = Modifier.scale(1.3F)
                    )
                }
            }

        }
    }
}

@Composable
fun DownloadProgressContent(downloadInfo: DownloadInfo) {

    val progress by downloadInfo.progressFlow.collectAsState()

    val speed by downloadInfo.speedFlow.collectAsState()

    val fileName = downloadInfo.fileName

    var isPaused by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "${getStringRes(R.string.Download)}: $fileName")
        Spacer(modifier = Modifier.height(10.dp))
        GradientLinearProgressIndicator(
            progress = progress,
            strokeCap = StrokeCap.Round,
            brush = Brush.linearGradient(
                listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.secondary
                )
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "${stringResource(R.string.Progress)}: ${(progress * 100).toInt()}%"
        )
        Text(
            text = "${stringResource(R.string.Speed)}: $speed ${stringResource(R.string.Speed_kbs)}"
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {

            CustomIconButton(
                onClick = {
                    isPaused = !isPaused
                    downloadInfo.actionPauseResume()
                },
                shape = MaterialTheme.shapes.medium,
                contentPadding = PaddingValues(6.dp)
            ) {
                Icon(
                    imageVector = if (isPaused) ImageVector.vectorResource(id = R.drawable.round_play_arrow_24) else ImageVector.vectorResource(
                        id = R.drawable.round_pause_24
                    ),
                    contentDescription = null,
                    modifier = Modifier.scale(1.3F)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            CustomIconButton(
                onClick = {
                    downloadInfo.actionCancel()
                },
                shape = MaterialTheme.shapes.medium,
                contentPadding = PaddingValues(6.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.round_close_24),
                    contentDescription = null,
                    modifier = Modifier.scale(1.3F)
                )
            }
        }
    }
}

private fun Duration.toMillisInt(): Int = this.toMillis().toInt()