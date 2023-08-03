package ru.blays.revanced.Elements.DataClasses

import kotlinx.coroutines.flow.StateFlow
import ru.blays.revanced.DeviceUtils.Root.ModuleIntstaller.ModuleInstaller

data class MagiskInstallerAlertDialogState(
    val statusFlow: StateFlow<ModuleInstaller.Status>? = null,
    val isExpanded: Boolean = false
)
