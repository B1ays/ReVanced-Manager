package ru.Blays.ReVanced.Manager

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import ru.Blays.ReVanced.Manager.UI.Theme.ReVancedManagerTheme
import ru.blays.revanced.Elements.Elements.CustomButton.CustomIconButton
import ru.blays.revanced.Elements.Elements.FloatingBottomMenu.surfaceColorAtAlpha
import ru.blays.revanced.Services.RootService.Util.isRootGranted
import ru.blays.revanced.shared.Extensions.copyToClipBoard
import ru.blays.revanced.shared.Extensions.share
import ru.blays.revanced.shared.R
import java.time.LocalDate
import java.time.LocalTime

private val additionalInfo: String
    get() =
"""date: ${LocalDate.now()} | ${LocalTime.now()}
android version: ${Build.VERSION.RELEASE} (${Build.VERSION.SDK_INT})
device model: ${Build.DEVICE}
device brand: ${Build.BRAND}
Supported abi: ${Build.SUPPORTED_ABIS.joinToString()}
Root granted & Magisk installed: $isRootGranted
============""".trimIndent() + "\n"


class CrashHandlerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val stackTrace = intent.getStringExtra("StackTrace") ?: ""

        val fullLog = additionalInfo + stackTrace

        val callback = onBackPressedDispatcher.addCallback(this, true) {
            finishAndRemoveTask()
        }

        setContent {

            ReVancedManagerTheme {

                Surface(color = MaterialTheme.colorScheme.background) {

                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth()
                    ) {

                        Row {

                            CustomIconButton(
                                onClick = { copyToClipBoard(fullLog) },
                                shape = CircleShape,
                                containerColor = MaterialTheme.colorScheme.surfaceColorAtAlpha(0.3F),
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ) {
                                Icon(imageVector = ImageVector.vectorResource(id = R.drawable.round_content_copy_24), contentDescription = null)
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            CustomIconButton(
                                onClick = { share(fullLog) },
                                shape = CircleShape,
                                containerColor = MaterialTheme.colorScheme.surfaceColorAtAlpha(0.3F),
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ) {
                                Icon(imageVector = ImageVector.vectorResource(id = R.drawable.round_share_24), contentDescription = null)
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
                                text = fullLog
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                    }
                }
            }
        }
    }
}
