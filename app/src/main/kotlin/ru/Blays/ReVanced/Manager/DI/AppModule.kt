package ru.Blays.ReVanced.Manager.DI

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import ru.Blays.ReVanced.Manager.UI.ViewModels.MainScreenViewModel
import ru.Blays.ReVanced.Manager.UI.ViewModels.VersionsListScreenViewModel
import ru.blays.revanced.Elements.Repository.SettingsRepository
import ru.blays.revanced.Services.PublicApi.PackageManagerApi
import ru.blays.revanced.Services.PublicApi.PackageManagerApiImpl
import ru.blays.revanced.data.repositories.AppInfoRepositoryImplementation
import ru.blays.revanced.domain.Repositories.AppInfoRepositoryInterface
import ru.blays.revanced.domain.UseCases.GetApkListUseCase
import ru.blays.revanced.domain.UseCases.GetChangelogUseCase
import ru.blays.revanced.domain.UseCases.GetVersionsListUseCase

private val settingsRepository: SettingsRepository by inject(SettingsRepository::class.java)

val appModule = module {
    viewModel { MainScreenViewModel(get()) }
    viewModel { VersionsListScreenViewModel(get(), get(), get()) }
    single<AppInfoRepositoryInterface> { AppInfoRepositoryImplementation() }
    factory<PackageManagerApi> { PackageManagerApiImpl(get(), settingsRepository.installerType) }
    factory<GetVersionsListUseCase> { GetVersionsListUseCase(get()) }
    factory<GetApkListUseCase> { GetApkListUseCase(get()) }
    factory<GetChangelogUseCase> { GetChangelogUseCase(get()) }
}