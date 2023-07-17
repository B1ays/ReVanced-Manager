package ru.blays.revanced.data.DataModels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppUpdateModel(
    @SerialName("Available version") val availableVersion: String,
    @SerialName("Version code") val versionCode: Int,
    @SerialName("Build date") val buildDate: String,
    @SerialName("Changelog link") val changelogLink: String,
    @SerialName("Apk link") val apkLink: String
)
