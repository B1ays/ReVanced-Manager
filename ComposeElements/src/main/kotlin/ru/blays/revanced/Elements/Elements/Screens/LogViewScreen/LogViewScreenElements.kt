package ru.blays.revanced.Elements.Elements.Screens.LogViewScreen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import ru.blays.revanced.Elements.Elements.CustomButton.CustomIconButton
import ru.blays.revanced.Elements.Elements.FloatingBottomMenu.surfaceColorAtAlpha
import ru.blays.revanced.shared.Extensions.defaultFormatter
import ru.blays.revanced.shared.Extensions.getCurrentFormattedTime
import ru.blays.revanced.shared.LogManager.BLog
import ru.blays.revanced.shared.R

private const val TAG = "LogViewElement"

@Composable
fun LogView(
    log: String,
    actionCopy: (String) -> Unit,
    actionShare: (String) -> Unit,
    actionSaveToFile: (Uri, String) -> Unit
) {
    LogView(
        log = AnnotatedString(log),
        actionCopy = actionCopy,
        actionShare = actionShare,
        actionSaveToFile = actionSaveToFile
    )
}

@Composable
fun LogView(
    log: AnnotatedString,
    actionCopy: (String) -> Unit,
    actionShare: (String) -> Unit,
    actionSaveToFile: (Uri, String) -> Unit
) {

    val scrollState = rememberScrollState()
    
    val register = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/text")
    ) { uri ->
        BLog.i(TAG, "Created uri: $uri")
        uri?.let { actionSaveToFile(it, log.text) }
    }

    Column(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
    ) {

        Row {

            CustomIconButton(
                onClick = { actionCopy(log.toString()) },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.surfaceColorAtAlpha(0.3F),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.round_content_copy_24),
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            CustomIconButton(
                onClick = { actionShare(log.toString()) },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.surfaceColorAtAlpha(0.3F),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.round_share_24),
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            CustomIconButton(
                onClick = {
                    val logName = "log_${getCurrentFormattedTime(defaultFormatter)}.log"
                    register.launch(logName)
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.surfaceColorAtAlpha(0.3F),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.round_save_as_24),
                    contentDescription = null
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Card(modifier = Modifier
            .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(7.dp),
                text = log
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}