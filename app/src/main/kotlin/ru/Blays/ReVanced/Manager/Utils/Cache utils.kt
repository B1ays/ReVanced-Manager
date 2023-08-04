package ru.Blays.ReVanced.Manager.Utils

import android.content.Context
import ru.blays.revanced.shared.Data.DEFAULT_INSTALLER_CACHE_FOLDER

fun clearInstallerCache(context: Context) {
    val dir = DEFAULT_INSTALLER_CACHE_FOLDER(context)
    val files = dir.listFiles()
    files?.forEach { file ->
        file.delete()
    }
}