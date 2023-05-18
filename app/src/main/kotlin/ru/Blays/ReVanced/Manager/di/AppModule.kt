package ru.Blays.ReVanced.Manager.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import ru.blays.revanced.Presentation.Repository.SettingsRepository
import ru.blays.revanced.Presentation.ViewModels.MainScreen.MainScreenViewModel
import ru.blays.revanced.Presentation.ViewModels.VersionsListScreenViewModel
import ru.blays.revanced.Services.PublicApi.PackageManagerApi
import ru.blays.revanced.Services.PublicApi.PackageManagerApiImpl
import ru.blays.revanced.data.repositories.AppInfoRepositoryImplementation
import ru.blays.revanced.data.repositories.SettingsRepositoryImplementation
import ru.blays.revanced.domain.Repositories.AppInfoRepositoryInterface
import ru.blays.revanced.domain.UseCases.GetApkListUseCase
import ru.blays.revanced.domain.UseCases.GetChangelogUseCase
import ru.blays.revanced.domain.UseCases.GetVersionsListUseCase

private val settingsRepository: SettingsRepository by inject(SettingsRepository::class.java)

val appModule = module {
    viewModel { MainScreenViewModel() }
    viewModel { VersionsListScreenViewModel(get(), get(), get()) }
    factory<GetVersionsListUseCase> { GetVersionsListUseCase(get()) }
    factory<GetApkListUseCase> { GetApkListUseCase(get()) }
    factory<GetChangelogUseCase> { GetChangelogUseCase(get()) }
    single<AppInfoRepositoryInterface> { AppInfoRepositoryImplementation() }
    single<SettingsRepositoryImplementation> { SettingsRepositoryImplementation(get()) }
    single<SettingsRepository> { SettingsRepository(get()) }
    factory<PackageManagerApi> { PackageManagerApiImpl(get(), settingsRepository.installerType) }
}