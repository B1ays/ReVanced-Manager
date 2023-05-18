package ru.blays.revanced.Presentation.ViewModels.MainScreen

import androidx.lifecycle.ViewModel
import ru.blays.revanced.Presentation.DataClasses.InstalledAppInfo

class MainScreenViewModel : ViewModel() {

    val appList = mutableListOf<InstalledAppInfo>()
    fun getAppList() {

    }
}