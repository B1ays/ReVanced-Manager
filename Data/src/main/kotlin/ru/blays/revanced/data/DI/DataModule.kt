package ru.blays.revanced.data.DI

import androidx.room.Room
import org.koin.dsl.module
import ru.blays.revanced.data.CacheManager.CacheManagerInterface
import ru.blays.revanced.data.CacheManager.Implementation.CacheManager
import ru.blays.revanced.data.CacheManager.Room.CacheDAO
import ru.blays.revanced.data.CacheManager.Room.CacheDatabase
import ru.blays.revanced.data.CacheManager.StorageUtils.CacheStorageUtils
import ru.blays.revanced.data.CacheManager.StorageUtils.StorageUtilsInterface
import ru.blays.revanced.data.repositories.AppInfoRepositoryImplementation
import ru.blays.revanced.data.repositories.SettingsRepositoryImplementation
import ru.blays.revanced.domain.Repositories.AppInfoRepositoryInterface

val dataModule = module {
    single<CacheDatabase> {
        Room.databaseBuilder(
            context = get(),
            klass = CacheDatabase::class.java,
            name = "CacheDB.db"
        ).build()
    }
    single<SettingsRepositoryImplementation> { SettingsRepositoryImplementation(get()) }
    factory<CacheDAO> { get<CacheDatabase>().getCacheDao() }
    factory<StorageUtilsInterface> { CacheStorageUtils(get()) }
    factory<CacheManagerInterface> { CacheManager(get(), get()) }
    single<AppInfoRepositoryInterface> {
        val cacheLifetimeLong = when(get<SettingsRepositoryImplementation>().cacheLifetimeLong) {
            0L -> 3L
            2L -> 6L
            4L -> 12L
            6L -> 24L
            8L -> 48L
            10L -> Int.MAX_VALUE.toLong()
            else -> 6L
        }
        AppInfoRepositoryImplementation(get(), cacheLifetimeLong)
    }
}