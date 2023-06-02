package ru.blays.revanced.data.DataModels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VersionsInfoModel(
    @SerialName("Version") val version: String? = null,
    @SerialName("Patches version") val patchesVersion: String? = null,
    @SerialName("Build date") val buildDate: String? = null,
    @SerialName("Changelog link") val changelogLink: String? = null,
    @SerialName("Versions list") val versionsListLink: String? = null
) {

}


