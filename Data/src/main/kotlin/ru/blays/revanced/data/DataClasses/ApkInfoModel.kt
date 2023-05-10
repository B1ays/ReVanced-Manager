package ru.blays.revanced.data.DataClasses

import kotlinx.serialization.Serializable

@Serializable
data class ApkInfoModel(
    val type: String,
    val name: String,
    val description: String,
    val url: String
)
