package ru.blays.revanced.data.DI

import androidx.room.Room
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.blays.revanced.data.CacheManager.CacheManagerInterface
import ru.blays.revanced.data.CacheManager.Implementation.CacheManager
import ru.blays.revanced.data.CacheManager.Room.CacheDAO
import ru.blays.revanced.data.CacheManager.Room.CacheDatabase
import ru.blays.revanced.data.CacheManager.StorageUtils.CacheStorageUtils
import ru.blays.revanced.data.CacheManager.StorageUtils.StorageUtilsInterface
import ru.blays.revanced.data.repositories.NetworkRepositoryImplementation
import ru.blays.revanced.domain.Repositories.NetworkRepositoryInterface

val dataModule = module {
    single<CacheDatabase> {
        Room.databaseBuilder(
            context = get(),
            klass = CacheDatabase::class.java,
            name = "CacheDB.db"
        ).build()
    }
    factory<CacheDAO> { get<CacheDatabase>().getCacheDao() }
    factory<StorageUtilsInterface> { CacheStorageUtils(get()) }
    factory<CacheManagerInterface> { CacheManager(get(), get()) }
    single<NetworkRepositoryInterface> {
        val cacheLifetimeLong: Long = get(named("cacheLifetimeLong"))
        NetworkRepositoryImplementation(get(), cacheLifetimeLong)
    }
}