package ru.blays.revanced.domain.DataClasses

data class VersionsInfoModelDto(
    val version : String? = null,
    val patchesVersion: String? = null,
    val buildDate: String,
    val changelogLink: String? = null,
    val versionsListLink: String? = null
)
