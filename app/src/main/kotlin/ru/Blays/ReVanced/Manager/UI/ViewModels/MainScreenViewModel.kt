package ru.Blays.ReVanced.Manager.UI.ViewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import ru.blays.revanced.domain.UseCases.GetVersionsListUseCase

class MainScreenViewModel(private val getVersionsListUseCase: GetVersionsListUseCase) : ViewModel(), CoroutineScope {

    override val coroutineContext = Dispatchers.IO

    fun getLatestVersionName(): Deferred<String?> {
        return async {
            val list = getVersionsListUseCase.execut(GetVersionsListUseCase.YOUTUBE)
            list.firstOrNull()?.version
        }
    }
}