package ru.blays.revanced.Elements.DataClasses

data class VersionInfo(
    val version: String,
    val patchesVersion: String,
    val date: String,
    val changelog: Changelog = Changelog()
)

data class Changelog(
    val added: List<String> = emptyList(),
    val changed: List<String> = emptyList(),
    val removed: List<String> = emptyList()
)
