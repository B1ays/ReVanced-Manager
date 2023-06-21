package ru.blays.revanced.data.CasheManager

interface CacheManagerInterface {

    fun addToCache(key: String, json: String): Boolean

    fun <T> addToCache(key: T, json: String): Boolean

    fun getJsonFromCache(key: String): String?

    fun <KEY, OUT> getJsonFromCache(key: KEY): OUT?

    fun removeFromCache(key: String): Boolean

}