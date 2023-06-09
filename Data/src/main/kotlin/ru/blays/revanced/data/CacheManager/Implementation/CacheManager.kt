package ru.blays.revanced.data.CacheManager.Implementation

import kotlinx.coroutines.coroutineScope
import ru.blays.revanced.data.CacheManager.CacheManagerInterface
import ru.blays.revanced.data.CacheManager.Room.CacheDAO
import ru.blays.revanced.data.CacheManager.Room.CacheStorageEntity
import ru.blays.revanced.data.CacheManager.StorageUtils.StorageUtilsInterface
import ru.blays.revanced.shared.Extensions.getCurrentFormattedTime
import ru.blays.revanced.shared.Extensions.isInRange
import java.text.SimpleDateFormat
import java.util.Locale

private const val format = "yyyy-MM-dd HH:mm"

private const val TAG = "CacheManager"

class CacheManager(private val storageUtils: StorageUtilsInterface, private val cacheDAO: CacheDAO): CacheManagerInterface {

    private val formatter = SimpleDateFormat(format, Locale.getDefault())

    override suspend fun addToCache(key: String, json: String): Boolean = coroutineScope {
        var file = storageUtils.getCacheFile(key)
        val hash = storageUtils.hashCode(key)
        if (file == null) {
            file = storageUtils.createCacheFile(key)
        }
        return@coroutineScope try {
            file.writeText(json)
            val time = getCurrentFormattedTime(formatter)
            if (cacheDAO.updateInfoInTable(hash, time) != 1) {
                cacheDAO.addToTable(
                    CacheStorageEntity(id = 0, fileHashCode = hash, creationTime = time)
                )
            }
            /*return*/ true
        } catch (_: Exception) { /*return*/ false }
    }

    override suspend fun <T> addToCache(key: T, json: String): Boolean = coroutineScope {
        // TODO("Not yet implemented")
        return@coroutineScope false
    }

    override suspend fun getJsonFromCache(key: String, cacheLifecycleLong: Long): String? = coroutineScope {
        val hash = storageUtils.hashCode(key)
        val isInRange: Boolean = try {
            val lastUpdateTimeString = cacheDAO.getInfoByName(hash).creationTime
            val dateObject = formatter.parse(lastUpdateTimeString)
            dateObject?.let { it.isInRange(cacheLifecycleLong) } ?: false
        } catch (_: Exception) { false }

        if (isInRange) {
            val file = storageUtils.getCacheFile(key)
            return@coroutineScope file?.readText()
        } else {
            return@coroutineScope null
        }
    }

    override suspend fun <KEY, OUT> getJsonFromCache(key: KEY): OUT? = coroutineScope {
        TODO("Not yet implemented")
    }

    override suspend fun removeFromCache(key: String): Boolean = coroutineScope {
        val hash = storageUtils.hashCode(key)
        cacheDAO.removeFromTableByHash(hash)
        return@coroutineScope storageUtils.deleteCacheFile(key)
    }
}