package ru.Blays.ReVanced.Manager.Utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import ru.Blays.ReVanced.Manager.Data.defaultAccentColorsList
import ru.blays.preference.DataStores.ColorAccentIndexDS
import ru.blays.preference.DataStores.CustomColorSelectedDS
import ru.blays.preference.DataStores.CustomColorValueDS
import ru.blays.revanced.Elements.Util.BuildedTheme
import ru.blays.revanced.Elements.Util.buildTheme

@Composable
fun buildedTheme(): BuildedTheme {
    val context = LocalContext.current
    val colorAccentIndexDs = remember {
        ColorAccentIndexDS(context)
    }
    val colorAccentIndexState by colorAccentIndexDs.asState()
    val customColorSelectedDS = remember {
        CustomColorSelectedDS(context)
    }
    val customColorSelectedState by customColorSelectedDS.asState()
    val customColorValueDS = remember {
        CustomColorValueDS(context)
    }
    val customColorValueState by customColorValueDS.asState()
    val currentColor: Color = when {
        customColorSelectedState -> Color(customColorValueState)
        !customColorSelectedState -> defaultAccentColorsList[colorAccentIndexState]
        else -> defaultAccentColorsList[1]
    }
    return buildTheme(currentColor)
}