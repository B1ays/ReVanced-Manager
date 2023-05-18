package ru.blays.revanced.Presentation.DataClasses

data class InstalledAppInfo(
    val appName: String,
    val version: String,
    val patchesVersion: String,
    val packageName: String
) {
    companion object {
        const val youtubePackageName = "com.google.android.youtube"
        const val musicPackageName = "com.google.android.apps.youtube.music"
    }
}