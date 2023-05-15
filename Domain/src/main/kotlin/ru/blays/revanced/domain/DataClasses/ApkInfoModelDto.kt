package ru.blays.revanced.domain.DataClasses

data class ApkInfoModelDto(
    val isRootVersion: Boolean,
    val name: String,
    val description: String,
    val url: String
)
