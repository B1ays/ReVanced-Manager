package ru.Blays.ReVanced.Manager.UI.ViewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

open class BaseViewModel: ViewModel(), CoroutineScope {
    override val coroutineContext = Dispatchers.IO
}