package ru.Blays.ReVanced.Manager

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import ru.Blays.ReVanced.Manager.UI.Theme.ReVancedManagerTheme
import ru.blays.revanced.Elements.Elements.Screens.LogViewScreen.LogView
import ru.blays.revanced.Services.Root.Util.isKSUInstalled
import ru.blays.revanced.Services.Root.Util.isMagiskInstalled
import ru.blays.revanced.Services.Root.Util.isRootGranted
import ru.blays.revanced.shared.Extensions.copyToClipBoard
import ru.blays.revanced.shared.Extensions.share
import ru.blays.revanced.shared.Extensions.writeTextByUri
import java.time.LocalDate
import java.time.LocalTime

private val additionalInfo: String get() =
"""date: ${LocalDate.now()} | ${LocalTime.now()}
android version: ${Build.VERSION.RELEASE} (${Build.VERSION.SDK_INT})
device model: ${Build.DEVICE}
device brand: ${Build.BRAND}
Supported abi: ${Build.SUPPORTED_ABIS.joinToString()}
Root granted: $isRootGranted
Magisk installed: $isMagiskInstalled
KernelSU installed: $isKSUInstalled
============""".trimIndent() + "\n"

class CrashHandlerActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val stackTrace = intent.getStringExtra("StackTrace") ?: ""

        val fullLog = additionalInfo + stackTrace

        onBackPressedDispatcher.addCallback(this, true) {
            finishAndRemoveTask()
        }

        setContent {
            ReVancedManagerTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    LogView(
                        log = fullLog,
                        actionCopy = ::copyToClipBoard,
                        actionShare = ::share,
                        actionSaveToFile = ::writeTextByUri
                    )
                }
            }
        }
    }
}
