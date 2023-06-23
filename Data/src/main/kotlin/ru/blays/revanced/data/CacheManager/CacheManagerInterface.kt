package ru.blays.revanced.data.CacheManager

interface CacheManagerInterface {

    suspend fun addToCache(key: String, json: String): Boolean

    suspend fun <T> addToCache(key: T, json: String): Boolean

    suspend fun getJsonFromCache(key: String, cacheLifecycleLong: Long): String?

    suspend fun <KEY, OUT> getJsonFromCache(key: KEY): OUT?

    suspend fun removeFromCache(key: String): Boolean

}