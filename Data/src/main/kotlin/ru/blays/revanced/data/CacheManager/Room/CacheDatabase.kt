package ru.blays.revanced.data.CacheManager.Room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 1,
    entities = [
        CacheStorageEntity::class
    ]
)
abstract class CacheDatabase: RoomDatabase() {

    abstract fun getCacheDao(): CacheDAO

}