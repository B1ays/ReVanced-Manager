package ru.blays.revanced.data.DataClasses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VersionsInfoModel(
    @SerialName("Version") var version : String? = null,
    @SerialName("Patches version") var patchesVersion: String? = null,
    @SerialName("Changelog link") var changelogLink: String? = null,
    @SerialName("Download link") var downloadLink: String? = null
) {

}


