package ru.blays.revanced.Elements.Elements.Screens.DownloadsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.blays.revanced.Elements.DataClasses.DefaultPadding
import ru.blays.revanced.Elements.Elements.CustomButton.CustomIconButton
import ru.blays.revanced.data.Downloader.DataClass.DownloadInfo
import ru.blays.revanced.shared.R

@Composable
fun DownloadItem(downloadInfo: DownloadInfo) {

    val height = 80.dp

    val backgroundColor = MaterialTheme.colorScheme.inverseOnSurface
    val overlayColor = MaterialTheme.colorScheme.primary.copy(alpha = .2F)
    val contentColor = MaterialTheme.colorScheme.onSurface

    val progress by downloadInfo.progressFlow.collectAsState()
    val speed by downloadInfo.speedFlow.collectAsState()

    val isDownloaded = progress == 1F

    var isPaused by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(DefaultPadding.CardDefaultPadding)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(.5F)
                .height(height)
                .background(
                    color = backgroundColor,
                    shape = MaterialTheme.shapes.large
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .background(
                        color = overlayColor,
                        shape = MaterialTheme.shapes.large
                    )
            )
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .align(Alignment.CenterStart),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(.5F)) {
                    Text(
                        modifier = Modifier,
                        text = downloadInfo.fileName,
                        style = MaterialTheme.typography.titleMedium,
                        color = contentColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (isDownloaded) {
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "${stringResource(id = R.string.File_size)}: ${(downloadInfo.file.length() / 1024 / 1024)} ${stringResource(id = R.string.File_size_Mb)}",
                            style = MaterialTheme.typography.titleSmall,
                            color = contentColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                if (!isDownloaded) {
                    Spacer(modifier = Modifier.width(5.dp))
                    Row(
                        modifier = Modifier
                            .weight(.4F)
                            .background(
                                color = MaterialTheme.colorScheme.background.copy(alpha = .5F),
                                shape = CircleShape
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            modifier = Modifier
                                .scale(1.5F)
                                .padding(vertical = 8.dp),
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_two_arrow),
                            contentDescription = null,
                            tint = contentColor
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = "$speed ${stringResource(id = R.string.Speed_kbs)}")
                        Spacer(modifier = Modifier.width(2.dp))
                    }
                }
            }
        }
        if (!isDownloaded) {
            Spacer(modifier = Modifier.width(10.dp))
            CustomIconButton(
                modifier = Modifier.size(height),
                onClick = {
                    isPaused = !isPaused
                    downloadInfo.actionPauseResume()
                },
                shape = MaterialTheme.shapes.large,
                contentPadding = PaddingValues(6.dp),
                containerColor = overlayColor,
                contentColor = contentColor
            ) {
                Icon(
                    imageVector = if (isPaused) ImageVector.vectorResource(id = R.drawable.round_play_arrow_24) else ImageVector.vectorResource(
                        id = R.drawable.round_pause_24
                    ),
                    contentDescription = null,
                    modifier = Modifier.scale(2F)
                )
            }
        }
    }


    /*Card(
        modifier = Modifier
            .padding(DefaultPadding.CardDefaultPadding)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(text = "${getStringRes(R.string.Download)}: ${downloadInfo.fileName}")
                Spacer(modifier = Modifier.height(10.dp))

            }
            Row {
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

            }

        }
    }*/
}