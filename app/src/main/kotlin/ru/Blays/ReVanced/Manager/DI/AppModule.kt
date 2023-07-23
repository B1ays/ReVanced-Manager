package ru.Blays.ReVanced.Manager.DI

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.Blays.ReVanced.Manager.Repository.DownloadsRepository
import ru.Blays.ReVanced.Manager.UI.ViewModels.AppUpdateScreenViewModel
import ru.Blays.ReVanced.Manager.UI.ViewModels.MainScreenViewModel
import ru.Blays.ReVanced.Manager.UI.ViewModels.VersionsListScreenViewModel
import ru.blays.preference.DataStores.CacheLifetimeDS
import ru.blays.preference.DataStores.InstallerTypeDS
import ru.blays.revanced.Services.PublicApi.PackageManagerApi
import ru.blays.revanced.Services.PublicApi.PackageManagerApiImpl
import ru.blays.revanced.domain.UseCases.GetApkListUseCase
import ru.blays.revanced.domain.UseCases.GetChangelogUseCase
import ru.blays.revanced.domain.UseCases.GetUpdateInfoUseCase
import ru.blays.revanced.domain.UseCases.GetVersionsListUseCase

val appModule = module {
    viewModel { MainScreenViewModel() }
    viewModel { VersionsListScreenViewModel(get(), get(), get(), get()) }
    viewModel { AppUpdateScreenViewModel(get(), get(), get(), get(), get()) }
    factory<GetVersionsListUseCase> { GetVersionsListUseCase(get()) }
    factory<GetApkListUseCase> { GetApkListUseCase(get()) }
    factory<GetChangelogUseCase> { GetChangelogUseCase(get()) }
    factory { GetUpdateInfoUseCase(get()) }
    factory<PackageManagerApi> {
        val installerType by InstallerTypeDS(get())
        PackageManagerApiImpl(get(), installerType)
    }
    factory<Long>(named("cacheLifetimeLong")) {
        val cacheLifetimeLong: Long by CacheLifetimeDS(get())
        return@factory when (cacheLifetimeLong) {
            0L -> 3L
            2L -> 6L
            4L -> 12L
            6L -> 24L
            8L -> 24L
            10L -> Long.MAX_VALUE
            else -> 6L
        }
    }
    single<DownloadsRepository> { DownloadsRepository() }
}