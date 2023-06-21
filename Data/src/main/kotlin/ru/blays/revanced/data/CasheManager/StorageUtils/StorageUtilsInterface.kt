package ru.blays.revanced.data.CasheManager.StorageUtils

import ru.blays.revanced.data.CasheManager.Data.FileAndName
import java.io.File

interface StorageUtilsInterface {
    val cacheDir: File

    fun createCacheFile(key: String): File

    fun getCacheFile(key: String): File?

    fun getFilesNames(): Array<FileAndName>

    fun deleteCacheFile(key: String): Boolean
}