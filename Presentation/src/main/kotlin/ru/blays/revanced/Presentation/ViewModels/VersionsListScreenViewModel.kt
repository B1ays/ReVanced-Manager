package ru.blays.revanced.Presentation.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import ru.blays.revanced.Presentation.DataClasses.InstalledAppInfo
import ru.blays.revanced.domain.DataClasses.ApkInfoModelDto
import ru.blays.revanced.domain.DataClasses.VersionsInfoModelDto
import ru.blays.revanced.domain.UseCases.GetApkListUseCase
import ru.blays.revanced.domain.UseCases.GetChangelogUseCase
import ru.blays.revanced.domain.UseCases.GetVersionsListUseCase

class VersionsListScreenViewModel(
    private val getVersionsListUseCase: GetVersionsListUseCase,
    private val getApkListUseCase: GetApkListUseCase,
    private val getChangelogUseCase: GetChangelogUseCase
) : ViewModel() {

    companion object {

        const val YOUTUBE = "YouTube"
        const val MUSIC = "Music"
    }

    var isRefreshing by mutableStateOf(false)

    var list by mutableStateOf(emptyList<VersionsInfoModelDto>())

    var isApkListBottomSheetExpanded = MutableStateFlow(false)

    var isChangelogBottomSheetExpanded = MutableStateFlow(false)

    var bottomSheetList = MutableStateFlow(emptyList<ApkInfoModelDto>())

    var changelog = ""

    val installedAppInfo = InstalledAppInfo(
        appName = "YouTube ReVanced",
        version = "1.16.39",
        patchesVersion = "1.161.1",
        packageName = "com.google.android.youtube"
    )

    suspend fun getList(appType: String) = withContext(Dispatchers.IO) {
        isRefreshing = true
        list = getVersionsListUseCase.execut(appType)
        isRefreshing = false
    }

    suspend fun showApkListBottomSheet(url: String) {
        bottomSheetList.emit(getApkListUseCase.execute(url) ?: emptyList())
        isApkListBottomSheetExpanded.emit(true)
    }

    suspend fun showChangelogBottomSheet(url: String) {
        changelog = getChangelogUseCase.execut(url)
        isChangelogBottomSheetExpanded.tryEmit(true)
    }
}