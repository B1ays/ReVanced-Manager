package ru.Blays.ReVanced.Manager.DI

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.Blays.ReVanced.Manager.Repository.MicroGVersionsRepository
import ru.Blays.ReVanced.Manager.Repository.SettingsRepository
import ru.Blays.ReVanced.Manager.Repository.YoutubeMusicVersionsRepository
import ru.Blays.ReVanced.Manager.Repository.YoutubeVersionsRepository
import ru.Blays.ReVanced.Manager.UI.ViewModels.MainScreenViewModel
import ru.Blays.ReVanced.Manager.UI.ViewModels.VersionsListScreenViewModel
import ru.blays.revanced.Services.PublicApi.PackageManagerApi
import ru.blays.revanced.Services.PublicApi.PackageManagerApiImpl
import ru.blays.revanced.data.repositories.AppInfoRepositoryImplementation
import ru.blays.revanced.data.repositories.SettingsRepositoryImplementation
import ru.blays.revanced.domain.Repositories.AppInfoRepositoryInterface
import ru.blays.revanced.domain.UseCases.GetApkListUseCase
import ru.blays.revanced.domain.UseCases.GetChangelogUseCase
import ru.blays.revanced.domain.UseCases.GetVersionsListUseCase

val appModule = module {
    viewModel { MainScreenViewModel(get()) }
    viewModel { VersionsListScreenViewModel(get(), get(), get()) }
    factory<GetVersionsListUseCase> { GetVersionsListUseCase(get()) }
    factory<GetApkListUseCase> { GetApkListUseCase(get()) }
    factory<GetChangelogUseCase> { GetChangelogUseCase(get()) }
    factory<PackageManagerApi> {
        val installerType = get<SettingsRepository>().installerType
        PackageManagerApiImpl(get(), installerType)
    }
    single<AppInfoRepositoryInterface> { AppInfoRepositoryImplementation() }
    single<SettingsRepositoryImplementation> { SettingsRepositoryImplementation(get()) }
    single<SettingsRepository> { SettingsRepository(get()) }
    single<YoutubeVersionsRepository> { YoutubeVersionsRepository(get()) }
    single<YoutubeMusicVersionsRepository> { YoutubeMusicVersionsRepository(get()) }
    single<MicroGVersionsRepository> { MicroGVersionsRepository(get()) }
}