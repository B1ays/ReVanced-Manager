package ru.blays.revanced.data.CacheManager.StorageUtils

import ru.blays.revanced.data.CacheManager.Data.FileAndName
import java.io.File

interface StorageUtilsInterface {

    val cacheDir: File

    val hashCode: (String) -> String

    suspend fun createCacheFile(key: String): File

    suspend fun getCacheFile(key: String): File?

    suspend fun getFilesNames(): Array<FileAndName>

    suspend fun deleteCacheFile(key: String): Boolean

}