package ru.blays.revanced.Presentation.Elements.Screens.SettingsScreen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import ru.blays.revanced.Presentation.DataClasses.CardShape
import ru.blays.revanced.Presentation.DataClasses.DefaultPadding
import ru.blays.revanced.Presentation.R

private val ModifierWithExpandAnimation = Modifier
    .animateContentSize(
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutLinearInEasing
        )
    )

@Composable
fun SettingsExpandableCard(title: String, subtitle: String = "", content: @Composable () -> Unit) {

    var isMenuExpanded by remember { mutableStateOf(false) }

    val onExpandChange = {
        isMenuExpanded = !isMenuExpanded
    }

    val transition = updateTransition(targetState = isMenuExpanded, label = null)

    val rotateValue by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = 300
            )
        },
        label = ""
    ) { expanded ->
        if (expanded) 180f else 0f
    }

    Card(
        modifier = ModifierWithExpandAnimation
            .padding(
                horizontal = DefaultPadding.CardHorizontalPadding,
                vertical = DefaultPadding.CardVerticalPadding
            )
            .fillMaxWidth()
            .toggleable(value = isMenuExpanded) { onExpandChange() },
        shape = CardShape.CardStandalone,
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
                    .fillMaxWidth(0.6F)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                if (subtitle != "") Text(
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
                contentDescription = "Arrow"
            )

        }
        if (isMenuExpanded) {
            content()
        }
    }
}

@Composable
private fun SettingsCardWithSwitch(title: String, description: String, state: Boolean, action: (Boolean) -> Unit) {
    Card(
        modifier = Modifier
            .padding(
                horizontal = DefaultPadding.CardHorizontalPadding,
                vertical = DefaultPadding.CardVerticalPadding
            )
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
                    .fillMaxWidth(0.6F)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Switch(
                checked = state,
                onCheckedChange = action
            )
        }
    }
}

@Composable
private fun SettingsRadioButtonWithTitle(title: String, state: Int, index: Int, action: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(vertical = 2.dp, horizontal = 12.dp)
            .fillMaxWidth()
            .clickable(onClick = action),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        RadioButton(
            selected = state == index,
            onClick = action
        )
        Text(modifier = Modifier.padding(start = 8.dp), text = title)
    }
}

/*
@Composable
private fun ColorPickerItem(
    settingsViewModel: SettingsScreenVM,
    mainViewModel: MainViewModel,
    item: AccentColorItem,
    index: Int
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .padding(4.dp)
            .clip(CircleShape)
            .background(color = item.accentDark)
            .clickable {
                settingsViewModel.changeAccentColor(index)
                mainViewModel.changeAccentColor(index)
            }
    )
}*/
