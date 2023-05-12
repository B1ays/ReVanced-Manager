package ru.blays.revanced.Presentation.Screens

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import org.koin.compose.koinInject
import ru.blays.revanced.Presentation.DataClasses.AccentColorItem
import ru.blays.revanced.Presentation.Elements.Screens.SettingsScreen.ColorPickerItem
import ru.blays.revanced.Presentation.Elements.Screens.SettingsScreen.SettingsCardWithSwitch
import ru.blays.revanced.Presentation.Elements.Screens.SettingsScreen.SettingsExpandableCard
import ru.blays.revanced.Presentation.Elements.Screens.SettingsScreen.SettingsRadioButtonWithTitle
import ru.blays.revanced.Presentation.Repository.SettingsRepository
import ru.blays.revanced.Presentation.Repository.ThemeModel
import ru.hh.toolbar.custom_toolbar.CollapsingTitle
import ru.hh.toolbar.custom_toolbar.CustomToolbar

@Destination
@Composable
fun SettingsScreen(
    settingsRepository: SettingsRepository = koinInject(),
    navController: NavController
) {
    Scaffold(
        topBar = {
            CustomToolbar(
                collapsingTitle = CollapsingTitle.large(titleText = "Настройки"),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "NavigateBack")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier.padding(padding)
        ) {
            AccentSelector(settingsRepository)
            ThemeSelector(repository = settingsRepository)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) MonetSettings(repository = settingsRepository)
        }
    }
}

@Composable
private fun ThemeSelector(repository: SettingsRepository) {
    SettingsExpandableCard(
        title = "Тема",
        subtitle = "Выбор темы приложения"
    ) {
        SettingsRadioButtonWithTitle(title = "Системная", state = repository.appTheme.themeCode!!, index = 0) {
            repository.appTheme = ThemeModel(themeCode = 0)
        }
        SettingsRadioButtonWithTitle(title = "Тёмная", state = repository.appTheme.themeCode!!, index = 1) {
            repository.appTheme = ThemeModel(themeCode = 1)
        }
        SettingsRadioButtonWithTitle(title = "Светлая", state = repository.appTheme.themeCode!!, index = 2) {
            repository.appTheme = ThemeModel(themeCode = 2)
        }
    }
}

@Composable
private fun MonetSettings(repository: SettingsRepository) {
    SettingsCardWithSwitch(title = "Monet цвета", description = "Использовать цвета из Monet", state = repository.monetTheme) {
        repository.monetTheme = !repository.monetTheme
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