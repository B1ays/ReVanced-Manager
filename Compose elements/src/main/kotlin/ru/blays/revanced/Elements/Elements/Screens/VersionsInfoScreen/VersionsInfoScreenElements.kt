package ru.blays.revanced.Elements.Elements.Screens.VersionsInfoScreen


import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.material3.TabPosition
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
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.blays.revanced.Elements.DataClasses.AppInfo
import ru.blays.revanced.Elements.DataClasses.DefaultPadding
import ru.blays.revanced.Elements.DataClasses.RootVersionDownloadModel
import ru.blays.revanced.Elements.Elements.FloatingBottomMenu.surfaceColorAtAlpha
import ru.blays.revanced.Elements.Elements.GradientProgressIndicator.GradientLinearProgressIndicator
import ru.blays.revanced.Elements.Util.getStringRes
import ru.blays.revanced.Presentation.R
import ru.blays.revanced.data.Utils.DownloadState
import ru.blays.revanced.domain.DataClasses.ApkInfoModelDto
import ru.blays.revanced.domain.DataClasses.VersionsInfoModelDto
import java.time.Duration
import kotlin.reflect.KSuspendFunction1

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
        Text(text = "${getStringRes(R.string.Installed_version)}: ${appInfo.version}")
        Text(text = "${getStringRes(R.string.Patches_version)}: ${appInfo.patchesVersion}")
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
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
            Button(
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
    actionShowApkList: KSuspendFunction1<String, Unit>
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
                                actionShowApkList(item.versionsListLink.orEmpty())
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
                Text(text = "${getStringRes(R.string.Version)}: ${item.version}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${getStringRes(R.string.Patches_version)}: ${item.patchesVersion}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${getStringRes(R.string.Build_date)}: ${item.buildDate}")
            }
        }
    }
}

/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VersionsInfoCard(
    item: VersionsInfoModelDto,
    actionShowChangelog: KSuspendFunction1<String, Unit>,
    actionShowApkList: KSuspendFunction1<String, Unit>
) {

    var isExpanded by remember {
        mutableStateOf(false)
    }

    val mainContent: @Composable () -> Unit = {
        Card(
            modifier = Modifier
                .padding(
                    vertical = DefaultPadding.CardVerticalPadding,
                    horizontal = DefaultPadding.CardHorizontalPadding
                )
                .fillMaxWidth(),
           *//* shape = RectangleShape,*//*
            onClick = {
                isExpanded = !isExpanded
            }
        ) {

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "${getStringRes(R.string.Version)}: ${item.version}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${getStringRes(R.string.Patches_version)}: ${item.patchesVersion}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${getStringRes(R.string.Build_date)}: ${item.buildDate}")
            }
        }
    }

    SubcomposeLayout(
        modifier = Modifier
            .fillMaxWidth()
    ) { constraints ->

        val mainCard = subcompose(slotId = Slots.MAIN, mainContent).map {
            it.measure(Constraints(
                maxWidth = constraints.maxWidth
            ))
        }

        val mainCardMaxSize = mainCard.fold(IntSize.Zero) { currentMax, placeable ->
            IntSize(
                width = maxOf(currentMax.width, placeable.width),
                height = maxOf(currentMax.height, placeable.height)
            )
        }

        val slidingCard = subcompose(slotId = Slots.SLIDING) {
            SlidingActionCard(
                targetHeight = mainCardMaxSize.height.toDp(),
                isExpanded = isExpanded,
                changelogLink = item.changelogLink,
                versionsListLink = item.versionsListLink,
                actionShowChangelog = actionShowChangelog,
                actionShowApkList = actionShowApkList
            )
        }.map {
            it.measure(Constraints(maxWidth = mainCardMaxSize.width))
        }

        val slidingCardMaxSize = slidingCard.fold(IntSize.Zero) { currentMax, placeable ->
            IntSize(
                width = maxOf(currentMax.width, placeable.width),
                height = maxOf(currentMax.height, placeable.height)
            )
        }

        layout(constraints.maxWidth, mainCardMaxSize.height) {
            mainCard[0].place(0, 0)
            slidingCard[0].place(0, mainCardMaxSize.height)
        }
    }
}

@Suppress("AnimateAsStateLabel")
@Composable
private fun SlidingActionCard(
    targetHeight: Dp,
    isExpanded: Boolean,
    changelogLink: String?,
    versionsListLink: String?,
    actionShowChangelog: KSuspendFunction1<String, Unit>,
    actionShowApkList: KSuspendFunction1<String, Unit>
) {

    val height by animateDpAsState(targetValue = if (isExpanded) targetHeight else 0.dp)

    Card(
        modifier = Modifier
            .padding(
                vertical = DefaultPadding.CardVerticalPadding,
                horizontal = DefaultPadding.CardHorizontalPadding
            )
            .fillMaxWidth()
            .height(height)
        *//*.clip(
            RoundedCornerShape(
                topStart = animatedCorners,
                bottomStart = 12.dp,
                bottomEnd = 12.dp,
                topEnd = animatedCorners
            )
        )*//*,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtAlpha(0.1f)
        )*//*,
            shape = RectangleShape*//*
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
                        changelogLink?.let { actionShowChangelog(it) }
                    }
                }
            ) {
                Text(text = getStringRes(R.string.Action_changelog))
            }

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        versionsListLink?.let { actionShowApkList(it) }
                    }
                }
            ) {
                Text(text = getStringRes(R.string.Action_download))
            }
        }
    }
}*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubversionsListBottomSheet(
    isExpanded: MutableStateFlow<Boolean>,
    list: MutableStateFlow<List<ApkInfoModelDto>>,
    actionDownloadNonRootVersion: (String, String) -> Unit,
    actionDownloadRootVersion: (RootVersionDownloadModel) -> Unit,
    rootItemBackground: Color,
    nonRootItemBackground: Color,
    rootVersionsPage: Boolean
) {

    val state = rememberModalBottomSheetState()

    LaunchedEffect(key1 = Unit) {
        state.expand()
    }

    val listState = list.collectAsState().value

    val filteredList = listState.filter {
        it.isRootVersion == rootVersionsPage
    }

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
                items(filteredList) {item ->
                    ApkListItem(
                        item = item,
                        actionDownloadNonRootVersion = actionDownloadNonRootVersion,
                        actionDownloadRootVersion = actionDownloadRootVersion,
                        hideBottomSheet = hideBottomSheet,
                        rootItemBackground = rootItemBackground ,
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

@Composable
fun DownloadProgressContent(downloadStateList: SnapshotStateList<DownloadState>) {

    LazyColumn(
        modifier = Modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        items(downloadStateList) { state ->

            val progress by state.progressFlow.collectAsState()

            val fileName by state.fileNameFlow.collectAsState()

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
                text = "${getStringRes(R.string.Progress)}: ${(progress * 100F).toInt()}%"
            )
        }
    }
}

@Composable
fun CustomTabIndicator(
    modifier: Modifier = Modifier,
    height: Dp = 3.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    currentPagePosition: TabPosition
) {
    Box(
        modifier
            .customTabIndicatorOffset(currentPagePosition)
            .height(height)
            .clip(RoundedCornerShape(topStart = height, topEnd = height))
            .background(color = color)

    )
}

@Suppress("AnimateAsStateLabel")
private fun Modifier.customTabIndicatorOffset(
    currentTabPosition: TabPosition
): Modifier = composed {
    val currentTabWidth by animateDpAsState(
        targetValue = currentTabPosition.width,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
    )
    val indicatorOffset by animateDpAsState(
        targetValue = currentTabPosition.left,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
    )

    val initialOffset = (currentTabWidth / 4)

    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = initialOffset + indicatorOffset)
        .width(currentTabWidth / 2)
}

private fun Duration.toMillisInt(): Int = this.toMillis().toInt()

private enum class Slots {
    MAIN,
    SLIDING;
}