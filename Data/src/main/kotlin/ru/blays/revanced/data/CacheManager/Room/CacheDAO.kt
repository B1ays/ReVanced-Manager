package ru.blays.revanced.data.CacheManager.Room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CacheDAO {

    @Query("SELECT * FROM Cache_table WHERE fileHashCode = :fileHashCode")
    suspend fun getInfoByName(fileHashCode: String): CacheInfoTuple

    @Insert(CacheStorageEntity::class)
    suspend fun addToTable(newInfo: CacheStorageEntity)

    @Query("UPDATE Cache_table SET creationTime = :newTime WHERE fileHashCode = :fileHashCode")
    suspend fun updateInfoInTable(fileHashCode: String, newTime: String): Int

    @Query("DELETE FROM Cache_table WHERE fileHashCode = :fileHashCode ")
    suspend fun removeFromTableByHash(fileHashCode: String)

}