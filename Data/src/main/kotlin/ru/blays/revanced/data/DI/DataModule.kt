package ru.blays.revanced.data.DI

import androidx.room.Room
import org.koin.dsl.module
import ru.blays.revanced.data.CacheManager.Room.CacheDAO
import ru.blays.revanced.data.CacheManager.Room.CacheDatabase

val dataModule = module {
    single<CacheDatabase> {
        Room.databaseBuilder(
            context = get(),
            klass = CacheDatabase::class.java,
            name = "CacheDB.db"
        ).build()
    }
    factory<CacheDAO> { get<CacheDatabase>().getCacheDao() }
}