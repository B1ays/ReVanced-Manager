package ru.Blays.ReVanced.Manager.Data

data class MagiskInstallerState(
    val origApkDownloaded: Boolean = false,
    val modApkDownloaded: Boolean = false,
    val origApkInstalled: Boolean = false,
    val modApkInstalled: Boolean = false
)
