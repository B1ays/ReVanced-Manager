package ru.blays.revanced.Elements.Elements.Screens.MainScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import ru.blays.revanced.Elements.DataClasses.Apps

import ru.blays.revanced.Elements.Util.getStringRes
import ru.blays.revanced.Presentation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppInfoCard(
    app: Apps,
    actionNavigateToVersionsListScreen: (String) -> Unit
) {

    // App icon default size
    val iconSize = 80.dp

    // Repository that provides info about app
    val repository = app.repository

    // element state from repository
    val version by repository.version.collectAsState()
    val availableVersion by repository.availableVersion.collectAsState()

    // Content
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        onClick = { actionNavigateToVersionsListScreen(repository.appType) },
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
    ) {

        Box(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = -iconSize / 2)
            ) {

                Icon(
                    imageVector = app.icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(iconSize),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(text = repository.appName, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(6.dp))
                    // Text elements with null check using let function
                    version?.let {
                        Text(text = "${getStringRes(R.string.Installed_version)}: $it")
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    availableVersion?.let { Text(text = "${getStringRes(R.string.Available_version)}: $it") }
                }
            }

            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.round_arrow_forward_ios_24),
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            )
        }
    }
}