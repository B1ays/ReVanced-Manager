package ru.blays.revanced.Elements.DI

import org.koin.dsl.module
import ru.blays.revanced.Elements.Repository.MicroGVersionsRepository
import ru.blays.revanced.Elements.Repository.SettingsRepository
import ru.blays.revanced.Elements.Repository.YoutubeMusicVersionsRepository
import ru.blays.revanced.Elements.Repository.YoutubeVersionsRepository
import ru.blays.revanced.data.repositories.SettingsRepositoryImplementation

val composeElementsModule = module {
    single<SettingsRepositoryImplementation> { SettingsRepositoryImplementation(get()) }
    single<SettingsRepository> { SettingsRepository(get()) }
    single<YoutubeVersionsRepository> { YoutubeVersionsRepository(get()) }
    single<YoutubeMusicVersionsRepository> { YoutubeMusicVersionsRepository(get()) }
    single<MicroGVersionsRepository> { MicroGVersionsRepository(get()) }
}