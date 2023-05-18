package ru.blays.revanced.Presentation.Screens

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
import com.vanced.manager.util.isRootGranted
import org.koin.compose.koinInject
import ru.blays.revanced.Presentation.DataClasses.AccentColorItem
import ru.blays.revanced.Presentation.Elements.Screens.SettingsScreen.ColorPickerItem
import ru.blays.revanced.Presentation.Elements.Screens.SettingsScreen.SettingsCardWithSwitch
import ru.blays.revanced.Presentation.Elements.Screens.SettingsScreen.SettingsExpandableCard
import ru.blays.revanced.Presentation.Elements.Screens.SettingsScreen.SettingsGroup
import ru.blays.revanced.Presentation.Elements.Screens.SettingsScreen.SettingsRadioButtonWithTitle
import ru.blays.revanced.Presentation.Repository.SettingsRepository
import ru.blays.revanced.Presentation.Repository.ThemeModel
import ru.blays.revanced.Presentation.Screens.destinations.AboutScreenDestination
import ru.blays.revanced.Presentation.Utils.isSAndAboveCompose
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
        modifier = Modifier
            .nestedScroll(
            scrollBehavior.nestedScrollConnection
        ),
        topBar = {
            CustomToolbar(
                collapsingTitle = CollapsingTitle.large(titleText = "Настройки"),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "NavigateBack")
                    }
                },
                actions = {
                    DropdownMenu(expanded = isSpinnerExpanded, onDismissRequest = changeExpanded) {
                        DropdownMenuItem(text = { Text(text = "О приложении") }, onClick = { navController.navigate(AboutScreenDestination) })
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
                .padding(padding)
                .fillMaxSize()
        ) {
            item {
                SettingsGroup(title = "Тема приложения") {
                    ThemeSelector(repository = settingsRepository)
                    isSAndAboveCompose { MonetColors(repository = settingsRepository) }
                    AccentSelector(settingsRepository)
                }
            }
            item {
                SettingsGroup(title = "Основные") {
                    RootedMode(repository = settingsRepository)
                    InstallerType(repository = settingsRepository)
                }
            }
        }
    }
}

@Composable
private fun ThemeSelector(repository: SettingsRepository) {
    SettingsExpandableCard(
        title = "Тема",
        subtitle = "Выбор темы приложения"
    ) {
        SettingsRadioButtonWithTitle(title = "Системная", checkedIndex = repository.appTheme.themeCode!!, index = 0) {
            repository.appTheme = ThemeModel(themeCode = 0)
        }
        SettingsRadioButtonWithTitle(title = "Тёмная", checkedIndex = repository.appTheme.themeCode!!, index = 1) {
            repository.appTheme = ThemeModel(themeCode = 1)
        }
        SettingsRadioButtonWithTitle(title = "Светлая", checkedIndex = repository.appTheme.themeCode!!, index = 2) {
            repository.appTheme = ThemeModel(themeCode = 2)
        }
    }
}

@Composable
private fun MonetColors(repository: SettingsRepository) {
    SettingsCardWithSwitch(title = "Monet цвета", subtitle = "Использовать цвета из Monet", state = repository.monetTheme) {
        repository.monetTheme = it
    }
}

@Composable
private fun AccentSelector(
    repository: SettingsRepository
) {
    SettingsExpandableCard(title = "Цвет акцента", subtitle = "Генерирует тему приложения на основе выбранного цвета") {
        LazyRow(modifier = Modifier.padding(12.dp)) {
            itemsIndexed(AccentColorItem.list)
            { index, item ->
                ColorPickerItem(repository, item, index)
            }
        }
    }
}

@Composable
fun RootedMode(repository: SettingsRepository) {
    SettingsCardWithSwitch(
        title = "Использовать Root",
        subtitle = "Разблокирует Root возможности приложения",
        state = repository.isRootMode,
        isSwitchEnabled = isRootGranted
    ) {
        repository.isRootMode = it
    }
}

@Composable
fun InstallerType(repository: SettingsRepository) {
    SettingsExpandableCard(title = "APK установщик", subtitle = "Используется для установки Non-Root версий и оригинального приложения") {
        SettingsRadioButtonWithTitle(
            title = "Классический установщик",
            checkedIndex = repository.installerType,
            index = 0
        ) {
            repository.installerType = 0
        }
        SettingsRadioButtonWithTitle(
            title = "Сессионный установщик",
            checkedIndex = repository.installerType,
            index = 1
        ) {
            repository.installerType = 1
        }
        SettingsRadioButtonWithTitle(
            title = "Root установщик",
            checkedIndex = repository.installerType,
            index = 2,
            enabled = isRootGranted
        ) {
            repository.installerType = 2
        }
        SettingsRadioButtonWithTitle(
            title = "Shizuku установщик",
            checkedIndex = repository.installerType,
            index = 3,
            enabled = false
        ) {
            repository.installerType = 3
        }
    }
}