package ru.Blays.ReVanced.Manager

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import ru.Blays.ReVanced.Manager.UI.Theme.ReVancedManagerTheme

@OptIn(ExperimentalFoundationApi::class)
class CrashHandlerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val stackTrace = intent.getStringExtra("StackTrace")
        val clipData = ClipData.newPlainText("StackTrace", stackTrace)

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, stackTrace)

        setContent {

            ReVancedManagerTheme {

                Surface(color = MaterialTheme.colorScheme.background) {
                    LazyColumn(
                        modifier = Modifier
                            .padding(12.dp)
                    ) {
                        stickyHeader {
                            Row {
                                IconButton(onClick = { clipboardManager.setPrimaryClip(clipData) }) {
                                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.round_content_copy_24), contentDescription = null)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                IconButton(onClick = { startActivity(Intent.createChooser(intent, "Send StackTrace")) }) {
                                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.round_share_24), contentDescription = null)
                                }
                            }
                            Divider()
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        item { Text(text = stackTrace ?: "") }
                    }
                }
            }
        }
    }
}