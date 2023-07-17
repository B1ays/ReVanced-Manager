package ru.blays.revanced.domain.DataClasses

data class AppUpdateModelDto(
    val availableVersion: String,
    val versionCode: Int,
    val buildDate: String,
    val changelogLink: String,
    val apkLink: String
)
