package ru.blays.revanced.data.CasheManager.Implementation

import ru.blays.revanced.data.CasheManager.CacheManagerInterface
import ru.blays.revanced.data.CasheManager.StorageUtils.StorageUtilsInterface

class CacheManager(private val storageUtils: StorageUtilsInterface): CacheManagerInterface {

    override fun addToCache(key: String, json: String): Boolean {
        var file = storageUtils.getCacheFile(key)
        if (file == null) {
            file = storageUtils.createCacheFile(key)
        }
        return try {
            file.writeText(json)
            true
        } catch (_: Exception) { false }
    }

    override fun <T> addToCache(key: T, json: String): Boolean {
        // TODO("Not yet implemented")
        return false
    }

    override fun getJsonFromCache(key: String): String? {
        val file = storageUtils.getCacheFile(key)
        return file?.readText()
    }

    override fun <KEY, OUT> getJsonFromCache(key: KEY): OUT? {
        TODO("Not yet implemented")
    }

    override fun removeFromCache(key: String): Boolean {
        return storageUtils.deleteCacheFile(key)
    }
}