package ru.blays.revanced.data.CacheManager.StorageUtils

import android.content.Context
import ru.blays.revanced.data.CacheManager.Data.FileAndName
import java.io.File

private const val CACHE_DIR_NAME = "Network cache"

class CacheStorageUtils(context: Context): StorageUtilsInterface {
    override val cacheDir: File = File(context.cacheDir, CACHE_DIR_NAME).apply {
        if(!exists()) mkdir()
    }

    override val hashCode: (String) -> String
        get() = { it.hashCode().toString() }

    override suspend fun createCacheFile(key: String): File {
        val hash = hashCode(key)
        return File(cacheDir, hash)
    }

    override suspend fun getCacheFile(key: String): File? {
        val hash = hashCode(key)
        val cacheFile = File(cacheDir, hash)
        return if (cacheFile.exists()) cacheFile else null
    }

    override suspend fun getFilesNames(): Array<FileAndName> {
        val namesArray = mutableListOf<FileAndName>()
        cacheDir.listFiles()?.forEach { file ->
            if (file != null) {
                namesArray.add(
                    FileAndName(
                        name = file.name,
                        file = file
                    )
                )
            }
        }
        return namesArray.toTypedArray()
    }

    override suspend fun deleteCacheFile(key: String): Boolean {
        val hash = hashCode(key)
        return File(cacheDir, hash).delete()
    }
}