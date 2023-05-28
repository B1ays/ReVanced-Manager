package ru.blays.revanced.Elements.DataClasses

import kotlinx.coroutines.flow.MutableStateFlow

data class AppInfo(
    val appName: String? = null,
    val version: MutableStateFlow<String?> = MutableStateFlow(null),
    val patchesVersion: MutableStateFlow<String?> = MutableStateFlow(null),
    val packageName: String? = null
)