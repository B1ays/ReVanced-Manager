package ru.blays.revanced.Elements.Elements.Screens.AppUpdateScreen

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import dev.jeziellago.compose.markdowntext.MarkdownText
import ru.blays.revanced.Elements.DataClasses.DefaultPadding
import ru.blays.revanced.Elements.Elements.CustomButton.CustomIconButton
import ru.blays.revanced.Elements.Elements.FloatingBottomMenu.surfaceColorAtAlpha
import ru.blays.revanced.shared.R

@Composable
fun UpdateInfoHeader(
    availableVersion: String?,
    versionCode: Int?,
    buildDate: String?,
    actionDownload: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(DefaultPadding.CardDefaultPadding)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            availableVersion?.let { Text(text = "${stringResource(id = R.string.Available_version_name)}: $it")
                Spacer(modifier = Modifier.height(8.dp))
            }
            versionCode?.let { Text(text = "${stringResource(id = R.string.Version_code)}: $it")
                Spacer(modifier = Modifier.height(8.dp))
            }
            buildDate?.let { Text(text = "${stringResource(id = R.string.Build_date)}: $it")
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        CustomIconButton(
            modifier = Modifier
                .padding(end = 10.dp),
            onClick = actionDownload,
            containerColor = MaterialTheme.colorScheme.surfaceColorAtAlpha(0.3F),
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            shadowElevation = 12.dp,
            shadowColor = MaterialTheme.colorScheme.primary.copy(alpha = .8F)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.round_download_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = .8F)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = stringResource(id = R.string.Action_install),
                color = MaterialTheme.colorScheme.primary.copy(alpha = .8F)
            )
        }
    }
}

@Composable
fun ChangelogView(changelog: String) {
    val scrollState = rememberScrollState()
    Card(modifier = Modifier
        .padding(DefaultPadding.CardDefaultPadding)
        .fillMaxWidth()
        .fillMaxHeight(.96F)
    ) {
        MarkdownText(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(7.dp),
            markdown = changelog,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}