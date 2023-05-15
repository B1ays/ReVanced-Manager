package ru.blays.revanced.Presentation.Elements.Screens.VersionsInfoScreen

import android.content.Context
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vanced.manager.installer.util.PM
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ru.blays.revanced.Presentation.DataClasses.DefaultPadding
import ru.blays.revanced.Presentation.DataClasses.InstalledAppInfo
import ru.blays.revanced.Presentation.DataClasses.NavBarExpandedContent
import ru.blays.revanced.Presentation.Elements.FloatingBottomMenu.surfaceColorAtAlpha
import ru.blays.revanced.Presentation.Elements.GradientProgressIndicator.GradientLinearProgressIndicator
import ru.blays.revanced.Presentation.R
import ru.blays.revanced.Presentation.Utils.createDownloadSession
import ru.blays.revanced.Presentation.ViewModels.VersionsListScreenViewModel
import ru.blays.revanced.Presentation.theme.cardBorderBlue
import ru.blays.revanced.Presentation.theme.cardBorderRed
import ru.blays.revanced.data.Utils.FileDownloader
import ru.blays.revanced.domain.DataClasses.ApkInfoModelDto
import ru.blays.revanced.domain.DataClasses.VersionsInfoModelDto
import java.time.Duration

@Composable
fun VersionsListScreenHeader(viewModel: VersionsListScreenViewModel, installedAppInfo: InstalledAppInfo)  {

    val context: Context = koinInject()

    Column(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = installedAppInfo.appName,
            style = MaterialTheme.typography.titleMedium
        )
        Text(text = "Версия: ${installedAppInfo.version}")
        Text(text = "Версия патчей: ${installedAppInfo.patchesVersion}")
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            OutlinedButton(onClick = { /*PM.uninstallPackage(installedAppInfo.packageName, context)*/ }) {
                Text(text = "Удалить")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                onClick = { PM.launchApp(pkg = installedAppInfo.packageName, context = context) }
            ) {
                Text(text = "Открыть")
            }
        }
        Divider(
            modifier = Modifier.padding(top = 6.dp),
            thickness = 2.dp)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VersionsInfoCard(item: VersionsInfoModelDto, viewModel: VersionsListScreenViewModel) {

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

    val animationDuration = Duration.ofSeconds(1).toMillisInt()

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
                        onClick = { CoroutineScope(Dispatchers.IO).launch { viewModel.showChangelogBottomSheet(item.changelogLink ?: "") } },
                        colors = ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text(text = "Changelog")
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(

                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.showApkListBottomSheet(item.versionsListLink.orEmpty())
                            }
                        }
                    ) {
                        Text(text = "Скачать")
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
                Text(text = "Версия: ${item.version}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Версия патчей: ${item.patchesVersion}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Дата сборки: ${item.buildDate}")
            }
        }
    }
    /*Rebugger(
        trackMap = mapOf(
            "item" to item,
            "isExpanded" to isExpanded,
            "isExist" to isExist,
            "localDensity" to localDensity,
            "mainCardHeight" to mainCardHeight,
            "slidedCardHeight" to slidedCardHeight,
            "animationDuration" to animationDuration,
            "offset" to offset,
            "bottomOffset" to bottomOffset,
            "animatedCorners" to animatedCorners
        ),
    )*/

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubversionsListBottomSheet(isExpanded: MutableStateFlow<Boolean>, list: MutableStateFlow<List<ApkInfoModelDto>>) {

    val state = rememberModalBottomSheetState()

    val listState = list.collectAsState().value

    Log.d("listLog", listState.getOrNull(0).toString())

    val hideBottomSheet = {
        isExpanded.tryEmit(false)
    }

    ModalBottomSheet(
        onDismissRequest = { hideBottomSheet() },
        sheetState = state,

        ) {
        LazyColumn {
            items(listState) {item ->
                ApkListItem(item = item, callback = hideBottomSheet)
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
        Spacer(Modifier.fillMaxHeight(0.1F))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangelogBottomSheet(isExpanded: MutableStateFlow<Boolean>, changelog: String) {

    val state = rememberModalBottomSheetState()

    val hideBottomSheet = {
        isExpanded.tryEmit(false)
    }

    ModalBottomSheet(
        onDismissRequest = { hideBottomSheet() },
        sheetState = state
        ) {
        MarkdownText(modifier = Modifier.padding(12.dp),
            markdown = changelog)
        Spacer(Modifier.fillMaxHeight(0.1F))
    }
}


@Composable fun ApkListItem(item: ApkInfoModelDto, callback: () -> Boolean) {

    val context = LocalContext.current

    Card(
        border = BorderStroke(
            color = if (item.isRootVersion) cardBorderRed else cardBorderBlue,
            width = 2.dp
        ),
        modifier = Modifier
            .padding(
                horizontal = DefaultPadding.CardHorizontalPadding,
                vertical = DefaultPadding.CardVerticalPadding
            )
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Описание: ${item.description}")
            }
            IconButton(
                onClick = {
                    callback()
                    val downloadSession = createDownloadSession(item.name, item.url, context)
                    NavBarExpandedContent.setContent { DownloadProgressContent(fileName = item.name, downloader = downloadSession) }
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

@Composable
private fun DownloadProgressContent(fileName: String, downloader: FileDownloader) {
    val progress = downloader.progressFlow.collectAsState().value

    val status = downloader.downloadStatusFlow.collectAsState().value

    if (status == FileDownloader.END_DOWNLOAD) NavBarExpandedContent.hide()

    Column(
        modifier = Modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Загрузка: $fileName")
        Spacer(modifier = Modifier.height(8.dp))
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
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Прогресс: ${progress * 100F}%"
        )
    }
}