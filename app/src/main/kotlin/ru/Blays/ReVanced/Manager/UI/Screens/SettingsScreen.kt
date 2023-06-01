package ru.Blays.ReVanced.Manager.UI.Screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import org.koin.compose.koinInject
import ru.Blays.ReVanced.Manager.Repository.SettingsRepository
import ru.Blays.ReVanced.Manager.Repository.ThemeModel
import ru.Blays.ReVanced.Manager.UI.Screens.destinations.AboutScreenDestination
import ru.Blays.ReVanced.Manager.Utils.isSAndAboveCompose
import ru.blays.revanced.Elements.DataClasses.AccentColorItem
import ru.blays.revanced.Elements.Elements.LazyItems.itemsGroupWithHeader
import ru.blays.revanced.Elements.Elements.Screens.SettingsScreen.ColorPickerItem
import ru.blays.revanced.Elements.Elements.Screens.SettingsScreen.SettingsCardWithSwitch
import ru.blays.revanced.Elements.Elements.Screens.SettingsScreen.SettingsCheckboxWithTitle
import ru.blays.revanced.Elements.Elements.Screens.SettingsScreen.SettingsExpandableCard
import ru.blays.revanced.Elements.Elements.Screens.SettingsScreen.SettingsRadioButtonWithTitle
import ru.blays.revanced.Elements.Util.getStringRes
import ru.blays.revanced.Presentation.R
import ru.blays.revanced.Services.RootService.Util.isRootGranted
import ru.hh.toolbar.custom_toolbar.CollapsingTitle
import ru.hh.toolbar.custom_toolbar.CustomToolbar
import ru.hh.toolbar.custom_toolbar.rememberToolbarScrollBehavior

@Destination
@Composable
fun SettingsScreen(
    settingsRepository: SettingsRepository = koinInject(),
    navController: NavController
) {

    val scrollBehavior = rememberToolbarScrollBehavior()
    
    var isSpinnerExpanded by remember { mutableStateOf(false) }
    
    val changeExpanded = { isSpinnerExpanded = !isSpinnerExpanded }

    Scaffold(
        topBar = {
            CustomToolbar(
                collapsingTitle = CollapsingTitle.large(titleText = getStringRes(R.string.AppBar_Settings)),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "NavigateBack")
                    }
                },
                actions = {
                    DropdownMenu(expanded = isSpinnerExpanded, onDismissRequest = changeExpanded) {
                        DropdownMenuItem(text = { Text(text = getStringRes(R.string.About_app)) }, onClick = { navController.navigate(AboutScreenDestination) })
                    }
                    IconButton(onClick = changeExpanded) {
                        Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = null)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(top = padding.calculateTopPadding())
                .fillMaxSize()
        ) {
            itemsGroupWithHeader(title = getStringRes(R.string.Settings_title_theme)) {
                ThemeSelector(repository = settingsRepository)
                isSAndAboveCompose { MonetColors(repository = settingsRepository) }
                AmoledTheme(repository = settingsRepository)
                AccentSelector(settingsRepository)
            }

            itemsGroupWithHeader(title = getStringRes(R.string.Settings_title_main)) {
                InstallerType(repository = settingsRepository)
                ManagedApps(repository = settingsRepository)
            }
        }
    }
}

@Composable
private fun ThemeSelector(repository: SettingsRepository) {
    SettingsExpandableCard(
        title = getStringRes(R.string.Settings_card_theme_title),
        subtitle = getStringRes(R.string.Settings_card_theme_description)
    ) {

        val themeCode = repository.appTheme.themeCode!!

        SettingsRadioButtonWithTitle(title = getStringRes(R.string.Settings_card_theme_system), checkedIndex = themeCode, index = 0) {
            repository.appTheme = ThemeModel(themeCode = 0)
        }
        SettingsRadioButtonWithTitle(title = getStringRes(R.string.Settings_card_theme_dark), checkedIndex = themeCode, index = 1) {
            repository.appTheme = ThemeModel(themeCode = 1)
        }
        SettingsRadioButtonWithTitle(title = getStringRes(R.string.Settings_card_theme_light), checkedIndex = themeCode, index = 2) {
            repository.appTheme = ThemeModel(themeCode = 2)
        }
    }
}

@Composable
private fun MonetColors(repository: SettingsRepository) {
    SettingsCardWithSwitch(
        title = getStringRes(R.string.Settings_card_monet_title),
        subtitle = getStringRes(R.string.Settings_card_monet_description),
        state = repository.monetTheme
    ) {
        repository.monetTheme = it
    }
}

@Composable
fun AmoledTheme(repository: SettingsRepository) {
    SettingsCardWithSwitch(
        title = getStringRes(R.string.Settings_card_amoled_title),
        subtitle = getStringRes(R.string.Settings_card_amoled_description),
        state = repository.isAmoledTheme
    ) {
        repository.isAmoledTheme = it
    }
}

@Composable
private fun AccentSelector(
    repository: SettingsRepository
) {

    val callback: (Int) -> Unit = { repository.accentColorItem = it }

    SettingsExpandableCard(
        title = getStringRes(R.string.Settings_card_accent_title),
        subtitle = getStringRes(R.string.Settings_card_accent_description)
    ) {
        LazyRow(modifier = Modifier.padding(12.dp)) {
            itemsIndexed(AccentColorItem.list)
            { index, item ->
                ColorPickerItem(item = item, index = index, callback = callback)
            }
        }
    }
}


@Composable
fun InstallerType(repository: SettingsRepository) {
    SettingsExpandableCard(
        title = getStringRes(R.string.Settings_card_installer_title),
        subtitle = getStringRes(R.string.Settings_card_installer_description)
    ) {
        /*SettingsRadioButtonWithTitle(
            title = "Классический установщик",
            checkedIndex = repository.installerType,
            index = 0
        ) {
            repository.installerType = 0
        }*/
        SettingsRadioButtonWithTitle(
            title = getStringRes(R.string.Settings_card_installer_session),
            checkedIndex = repository.installerType,
            index = 1
        ) {
            repository.installerType = 1
        }
        SettingsRadioButtonWithTitle(
            title =  getStringRes(R.string.Settings_card_installer_root),
            checkedIndex = repository.installerType,
            index = 2,
            enabled = isRootGranted
        ) {
            repository.installerType = 2
        }
        SettingsRadioButtonWithTitle(
            title =  getStringRes(R.string.Settings_card_installer_shizuku),
            checkedIndex = repository.installerType,
            index = 3,
            enabled = false
        ) {
            repository.installerType = 3
        }
    }
}

@Composable
fun ManagedApps(repository: SettingsRepository) {
    SettingsExpandableCard(
        title = getStringRes(R.string.Settings_card_selected_apps_title),
        subtitle = getStringRes(R.string.Settings_card_selected_apps_description)
    ) {
        SettingsCheckboxWithTitle(title = "YouTube Revanced", state = repository.youtubeManaged) { newValue ->
            repository.youtubeManaged = newValue
        }
        SettingsCheckboxWithTitle(title = "YouTube Music Revanced", state = repository.musicManaged) { newValue ->
            repository.musicManaged = newValue
        }
        SettingsCheckboxWithTitle(title = "Vanced MicroG", state = repository.microGManaged) { newValue ->
            repository.microGManaged = newValue
        }
    }
}