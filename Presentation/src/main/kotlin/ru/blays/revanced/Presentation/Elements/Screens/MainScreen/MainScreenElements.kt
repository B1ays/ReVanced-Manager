package ru.blays.revanced.Presentation.Elements.Screens.MainScreen

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import ru.blays.revanced.Presentation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppInfoCard(
    icon: ImageVector,
    title: String,
    currentVersion: String,
    availableVersion: String,
    onClick: () -> Unit
) {

    val iconSize = 80.dp

    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        onClick = onClick,
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
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(iconSize),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(text = title, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Установленная версия: $currentVersion")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Последняя версия: $availableVersion")
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