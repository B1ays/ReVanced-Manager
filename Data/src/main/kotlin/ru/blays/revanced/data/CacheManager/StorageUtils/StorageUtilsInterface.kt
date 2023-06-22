package ru.blays.revanced.data.CacheManager.StorageUtils

import ru.blays.revanced.data.CacheManager.Data.FileAndName
import java.io.File

interface StorageUtilsInterface {
    val cacheDir: File

    fun createCacheFile(key: String): File

    fun getCacheFile(key: String): File?

    fun getFilesNames(): Array<FileAndName>

    fun deleteCacheFile(key: String): Boolean
}