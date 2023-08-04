package ru.Blays.ReVanced.Manager.UI.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import ru.blays.helios.androidx.AndroidScreen
import ru.blays.helios.navigator.LocalNavigator
import ru.blays.helios.navigator.currentOrThrow
import ru.blays.revanced.Elements.DataClasses.CardShape
import ru.blays.revanced.Elements.DataClasses.DefaultPadding
import ru.blays.revanced.shared.R
import ru.hh.toolbar.custom_toolbar.CollapsingTitle
import ru.hh.toolbar.custom_toolbar.CustomToolbar
import ru.hh.toolbar.custom_toolbar.rememberToolbarScrollBehavior

class DeveloperScreen: AndroidScreen() {
    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val scrollState = rememberScrollState()
        val scrollBehavior = rememberToolbarScrollBehavior()

        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                CustomToolbar(
                    collapsingTitle = CollapsingTitle.large(titleText = stringResource(R.string.AppBar_Developer_menu)),
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        IconButton(
                            onClick = navigator::pop
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = .8F)
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(top = padding.calculateTopPadding())
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                DevMenuCard(
                    title = stringResource(id = R.string.App_logs),
                    subtitle = stringResource(id = R.string.App_logs_description)
                ) {
                    navigator.push(LogViewerScreen())
                }
                DevMenuCard(
                    title = stringResource(id = R.string.Crash_app),
                    subtitle = stringResource(id = R.string.Crash_app_description)
                ) {
                    throw RuntimeException("Test crash!")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevMenuCard(
    title: String,
    subtitle: String = "",
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(DefaultPadding.CardDefaultPadding)
            .fillMaxWidth()
            .clip(CardShape.CardStandaloneLarge),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(DefaultPadding.CardDefaultPaddingLarge)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                imageVector = ImageVector.vectorResource(
                    id = R.drawable.round_arrow_forward_ios_24
                ),
                contentDescription = null
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}