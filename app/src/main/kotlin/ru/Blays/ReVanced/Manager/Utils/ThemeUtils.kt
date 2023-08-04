package ru.Blays.ReVanced.Manager.Utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import org.koin.compose.koinInject
import ru.Blays.ReVanced.Manager.Data.defaultAccentColorsList
import ru.blays.preference.DataStores.ColorAccentIndexDS
import ru.blays.preference.DataStores.CustomColorSelectedDS
import ru.blays.preference.DataStores.CustomColorValueDS
import ru.blays.revanced.Elements.Util.BuildedTheme
import ru.blays.revanced.Elements.Util.buildTheme

@Composable
fun buildedTheme(): BuildedTheme {
    val colorAccentIndexDs: ColorAccentIndexDS = koinInject()
    val colorAccentIndexState by colorAccentIndexDs.asState()
    val customColorSelectedDS: CustomColorSelectedDS = koinInject()
    val customColorSelectedState by customColorSelectedDS.asState()
    val customColorValueDS: CustomColorValueDS = koinInject()
    val customColorValueState by customColorValueDS.asState()
    val currentColor: Color = when {
        customColorSelectedState -> Color(customColorValueState)
        !customColorSelectedState -> defaultAccentColorsList[colorAccentIndexState]
        else -> defaultAccentColorsList[1]
    }
    return buildTheme(currentColor)
}