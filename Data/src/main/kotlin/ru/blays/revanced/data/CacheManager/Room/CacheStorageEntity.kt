package ru.blays.revanced.data.CacheManager.Room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Cache_table")
data class CacheStorageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val fileHashCode: String,
    val creationTime: String
)
