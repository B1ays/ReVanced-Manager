package ru.blays.preference.DI

import org.koin.dsl.module
import ru.blays.preference.DataStores.AmoledThemeDS
import ru.blays.preference.DataStores.CacheLifetimeDS
import ru.blays.preference.DataStores.ColorAccentIndexDS
import ru.blays.preference.DataStores.CustomColorSelectedDS
import ru.blays.preference.DataStores.CustomColorValueDS
import ru.blays.preference.DataStores.DownloadsFolderUriDS
import ru.blays.preference.DataStores.InstallerTypeDS
import ru.blays.preference.DataStores.MonetColorsDS
import ru.blays.preference.DataStores.StorageAccessTypeDS
import ru.blays.preference.DataStores.ThemeDS

val preferencesModule = module {
    single { AmoledThemeDS(get()) }
    single { CacheLifetimeDS(get()) }
    single { ColorAccentIndexDS(get()) }
    single { CustomColorValueDS(get()) }
    single { CustomColorSelectedDS(get()) }
    single { DownloadsFolderUriDS(get()) }
    single { InstallerTypeDS(get()) }
    single { MonetColorsDS(get()) }
    single { StorageAccessTypeDS(get()) }
    single { ThemeDS(get()) }
}