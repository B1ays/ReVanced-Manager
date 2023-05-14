package ru.blays.revanced.data.DataClasses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VersionsInfoModel(
    @SerialName("Version") val version : String,
    @SerialName("Patches version") val patchesVersion: String,
    @SerialName("Build date") val buildDate: String,
    @SerialName("Changelog link") val changelogLink: String,
    @SerialName("Versions list") val versionsListLink: String
) {

}


