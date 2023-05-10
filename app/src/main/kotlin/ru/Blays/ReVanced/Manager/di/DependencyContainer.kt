package ru.Blays.ReVanced.Manager.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import ru.blays.revanced.Presentation.ViewModels.MainScreen.MainScreenViewModel
import ru.blays.revanced.Presentation.ViewModels.MainScreen.MainScreenViewModelFactory
import ru.blays.revanced.Presentation.ViewModels.VersionsListScreenViewModel


class DependencyContainer(context: ViewModelStoreOwner) {

    val mainScreenViewModel by lazy {
        ViewModelProvider(
            context,
            MainScreenViewModelFactory()
        )[MainScreenViewModel::class.java]
    }

    val versionsListScreenViewModel by lazy {
        ViewModelProvider(
            context,
            VersionsListScreenViewModel.Factory
        )[VersionsListScreenViewModel::class.java]
    }


}