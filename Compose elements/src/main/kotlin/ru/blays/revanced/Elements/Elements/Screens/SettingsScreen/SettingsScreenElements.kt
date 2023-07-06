package ru.blays.revanced.Elements.Elements.Screens.SettingsScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.blays.revanced.Elements.DataClasses.AccentColorItem
import ru.blays.revanced.Elements.DataClasses.CardShape
import ru.blays.revanced.Elements.DataClasses.DefaultPadding
import ru.blays.revanced.Elements.Elements.FloatingBottomMenu.surfaceColorAtAlpha
import ru.blays.revanced.shared.R

const val ANIMATION_DURATION_MILLIS = 300


@Suppress("TransitionPropertiesLabel")
@Composable
fun SettingsExpandableCard(
    title: String,
    subtitle: String = "",
    content: @Composable () -> Unit
) {

    var isMenuExpanded by remember { mutableStateOf(false) }

    val transition = updateTransition(targetState = isMenuExpanded, label = null)

    val rotateValue by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = ANIMATION_DURATION_MILLIS
            )
        }
    ) { expanded ->
        if (expanded) 180f else 0f
    }

    Card(
        modifier = Modifier
            .padding(DefaultPadding.CardDefaultPadding)
            .fillMaxWidth()
            .clip(CardShape.CardStandalone)
            .toggleable(value = isMenuExpanded) { isMenuExpanded = it },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.7F)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                if (subtitle.isNotEmpty()) Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Icon(
                modifier = Modifier
                    .scale(1.5F)
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = CircleShape
                    )
                    .rotate(rotateValue),
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_down_24dp),
                contentDescription = null
            )
        }
        AnimatedVisibility(
            visible = isMenuExpanded,
            enter = slideInVertically(
                animationSpec = spring(stiffness = 300F, dampingRatio = .6F),
                initialOffsetY = { -it / 2 }
            ) + expandVertically(),
            exit = slideOutVertically(
                animationSpec = spring(stiffness = 300F, dampingRatio = .6F),
                targetOffsetY = { -it / 2 }
            ) + shrinkVertically()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = CardShape.CardStandalone,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtAlpha(
                        0.15f
                    )
                )
            ) {
                content()
            }
        }
    }
}

@Composable
fun SettingsCardWithSwitch(
    title: String,
    subtitle: String,
    state: Boolean,
    isSwitchEnabled: Boolean = true,
    action: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(DefaultPadding.CardDefaultPadding)
            .clip(CardShape.CardStandalone)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 5.dp, horizontal = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.7F)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Switch(
                checked = state,
                onCheckedChange = action,
                enabled = isSwitchEnabled
            )
        }
    }
}

@Composable
fun SettingsRadioButtonWithTitle(
    title: String,
    checkedIndex: Int,
    enabled: Boolean = true,
    index: Int,
    action: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(vertical = 2.dp, horizontal = 12.dp)
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = action),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        RadioButton(
            selected = checkedIndex == index,
            onClick = action,
            enabled = enabled
        )
        Text(modifier = Modifier.padding(start = 8.dp), text = title)
    }
}

@Composable
fun SettingsCheckboxWithTitle(
    title: String,
    state: Boolean,
    enabled: Boolean = true,
    action: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(vertical = 2.dp, horizontal = 12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    )
    {
        Text(modifier = Modifier.padding(start = 8.dp), text = title)
        Checkbox(
            checked = state,
            onCheckedChange = action,
            enabled = enabled
        )
    }
}

@Composable
fun ColorPickerItem(
    item: AccentColorItem,
    index: Int,
    callback: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .padding(4.dp)
            .clip(CircleShape)
            .background(color = item.accentDark)
            .clickable { callback(index) }
    )
}

@Suppress("AnimatedContentLabel")
@Composable
fun CurrentSegment(
    currentSegment: Segment,
    modifier: Modifier,
    alignment: Alignment
) {
    Box(modifier = modifier, contentAlignment = alignment) {
        AnimatedContent(
            targetState = currentSegment,
            transitionSpec = {
                if (targetState.start > initialState.start) {
                    (slideInVertically { height -> height } + fadeIn()).togetherWith(
                        slideOutVertically { height -> -height } + fadeOut())
                } else {
                    (slideInVertically { height -> -height } + fadeIn()).togetherWith(
                        slideOutVertically { height -> height } + fadeOut())
                }.using(
                    SizeTransform(clip = false)
                )
            }
        ) { currentSegment ->
            Text(
                text = currentSegment.name,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

data class Segment(
    val start: Float,
    val name: String
)