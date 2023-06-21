package ru.blays.revanced.data.CasheManager.StorageUtils

import android.content.Context
import ru.blays.revanced.data.CasheManager.Data.FileAndName
import java.io.File

class CacheStorageUtils(context: Context): StorageUtilsInterface {
    override val cacheDir: File = context.cacheDir

    val hashCode: (String) -> String
        get() = { it.hashCode().toString() }

    override fun createCacheFile(key: String): File {
        val hash = hashCode(key)
        return File(cacheDir, hash)
    }

    override fun getCacheFile(key: String): File? {
        val hash = hashCode(key)
        val cacheFile = File(cacheDir, hash)
        return if (cacheFile.exists()) cacheFile else null
    }

    override fun getFilesNames(): Array<FileAndName> {
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

    override fun deleteCacheFile(key: String): Boolean {
        val hash = hashCode(key)
        return File(cacheDir, hash).delete()
    }
}