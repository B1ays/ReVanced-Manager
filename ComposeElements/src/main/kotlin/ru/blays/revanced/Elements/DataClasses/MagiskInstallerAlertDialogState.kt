package ru.blays.revanced.Elements.DataClasses

import kotlinx.coroutines.flow.StateFlow
import ru.blays.revanced.Services.Root.MagiskInstaller

data class MagiskInstallerAlertDialogState(
    val statusFlow: StateFlow<MagiskInstaller.Status>? = null,
    val isExpanded: Boolean = false
)
