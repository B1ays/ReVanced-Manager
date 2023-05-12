package ru.blays.revanced.Presentation.Elements.Screens.VersionsInfoScreen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.theapache64.rebugger.Rebugger
import ru.blays.revanced.Presentation.DataClasses.DefaultPadding
import ru.blays.revanced.Presentation.DataClasses.NavBarExpandedContent
import ru.blays.revanced.Presentation.Elements.FloatingBottomMenu.surfaceColorAtAlpha
import ru.blays.revanced.Presentation.Elements.GradientProgressIndicator.GradientLinearProgressIndicator
import ru.blays.revanced.domain.DataClasses.VersionsInfoModelDto
import java.time.Duration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VersionsInfoCard(item: VersionsInfoModelDto) {

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
                    .offset(y = offset),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtAlpha(0.1f)
                ),
                shape = RoundedCornerShape(
                    topStart = animatedCorners,
                    bottomStart = 12.dp,
                    bottomEnd = 12.dp,
                    topEnd = animatedCorners
                )
            ) {

                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = { NavBarExpandedContent.hide() },
                        colors = ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text(text = "Changelog")
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            NavBarExpandedContent.setContent { DownloadProgressContent("YouTube ReVanced 1.12.10") }
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
                },
            shape = RoundedCornerShape(topEnd = 12.dp, topStart = 12.dp, bottomEnd = animatedCorners, bottomStart = animatedCorners),
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
    Rebugger(
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
    )
}

private fun Duration.toMillisInt(): Int = this.toMillis().toInt()

@Composable
private fun DownloadProgressContent(fileName: String) {
    Column(
        modifier = Modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Загрузка: $fileName")
        Spacer(modifier = Modifier.height(8.dp))
        GradientLinearProgressIndicator(
            progress = .9F,
            strokeCap = StrokeCap.Round,
            brush = Brush.linearGradient(
                listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.onPrimary
                )
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Прогресс: 90%"
        )
    }
}