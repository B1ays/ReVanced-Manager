package ru.Blays.ReVanced.Manager.UI.Screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import ru.blays.helios.androidx.AndroidScreen
import ru.blays.revanced.Elements.Elements.Screens.LogViewScreen.LogView
import ru.blays.revanced.shared.Extensions.copyToClipBoard
import ru.blays.revanced.shared.Extensions.share
import ru.blays.revanced.shared.Extensions.writeTextByUri
import ru.blays.revanced.shared.LogManager.BLog


class LogViewerScreen: AndroidScreen() {

    @Composable
    override fun Content() {
        val formattedLog = BLog.getFormattedLog()
        val context = LocalContext.current

        LogView(
            log = formattedLog,
            actionCopy = context::copyToClipBoard,
            actionShare = context::share,
            actionSaveToFile = context::writeTextByUri
        )
    }
}