package ru.blays.revanced.Presentation.Elements.Screens.VersionsInfoScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import ru.blays.revanced.Presentation.DataClasses.DefaultPadding
import ru.blays.revanced.Presentation.DataClasses.NavBarExpandedContent
import ru.blays.revanced.Presentation.R
import ru.blays.revanced.domain.DataClasses.VersionsInfoModelDto

@Composable
fun VersionsInfoCard(item: VersionsInfoModelDto) {
    Card(
        modifier = Modifier
            .padding(
                vertical = DefaultPadding.CardVerticalPadding,
                horizontal = DefaultPadding.CardHorizontalPadding
            )
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Версия: ${item.version}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Версия патчей: ${item.patchesVersion}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Ссылка на Changelog: ${item.changelogLink}")
            }
            Icon(
                modifier = Modifier
                    .padding(12.dp)
                    .scale(1.7F)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable {
                        NavBarExpandedContent.bottomNavBarExpandedContent.tryEmit(
                            NavBarExpandedContent(
                                isExpanded = true
                            ) {
                                DownloadProgressContent()
                            }
                        )
                    },
                imageVector = ImageVector.vectorResource(id = R.drawable.round_arrow_downward_24),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun DownloadProgressContent() {
    Column(
        modifier = Modifier
            .padding(8.dp)
    ) {
        Text(text = "Загрузка")
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = .4F,
            strokeCap = StrokeCap.Round
        )
    }
}