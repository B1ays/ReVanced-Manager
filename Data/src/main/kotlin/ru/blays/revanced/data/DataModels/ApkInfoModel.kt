package ru.blays.revanced.data.DataModels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApkInfoModel(
    @SerialName("Apk name") val name: String,
    @SerialName("isRootVersion") val isRootVersion: Boolean,
    @SerialName("Short description") val description: String,
    @SerialName("Apk link") val url: String
)
