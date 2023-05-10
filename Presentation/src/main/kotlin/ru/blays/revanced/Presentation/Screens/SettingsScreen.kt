package ru.blays.revanced.Presentation.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import ru.blays.revanced.Presentation.Elements.Screens.SettingsScreen.SettingsExpandableCard
import ru.hh.toolbar.custom_toolbar.CollapsingTitle
import ru.hh.toolbar.custom_toolbar.CustomToolbar

@Destination
@Composable
fun SettingsScreen() {
    Scaffold(
        topBar = {
            CustomToolbar(
                collapsingTitle = CollapsingTitle.large(titleText = "Настройки")
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier.padding(padding)
        ) {
            AccentSelector()
        }
    }
}

@Composable
private fun AccentSelector(
    
) {

    SettingsExpandableCard(title = "Цвет акцента", subtitle = "Генерирует тему приложения на основе выбранного цвета") {
        LazyRow(modifier = Modifier.padding(12.dp)) {
           /* itemsIndexed(AccentColorList.list)
            { index, item ->
                ColorPickerItem(settingsViewModel, mainViewModel, item = item, index = index)
            }*/
        }
    }
}