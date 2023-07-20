package ru.Blays.ReVanced.Manager.UI.Screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import ru.Blays.ReVanced.Manager.BuildConfig
import ru.Blays.ReVanced.Manager.Data.defaultAccentColorsList
import ru.Blays.ReVanced.Manager.Repository.SettingsRepository
import ru.Blays.ReVanced.Manager.Repository.ThemeModel
import ru.Blays.ReVanced.Manager.UI.Navigation.shouldHideNavigationBar
import ru.Blays.ReVanced.Manager.UI.Theme.rainbowColors
import ru.Blays.ReVanced.Manager.Utils.isSAndAboveCompose
import ru.blays.helios.androidx.AndroidScreen
import ru.blays.helios.core.Screen
import ru.blays.helios.dialogs.LocalDialogNavigator
import ru.blays.helios.navigator.LocalNavigator
import ru.blays.helios.navigator.currentOrThrow
import ru.blays.revanced.Elements.Elements.LazyItems.itemsGroupWithHeader
import ru.blays.revanced.Elements.Elements.Screens.SettingsScreen.ColorPickerDialogContent
import ru.blays.revanced.Elements.Elements.Screens.SettingsScreen.ColorPickerItem
import ru.blays.revanced.Elements.Elements.Screens.SettingsScreen.CurrentSegment
import ru.blays.revanced.Elements.Elements.Screens.SettingsScreen.Segment
import ru.blays.revanced.Elements.Elements.Screens.SettingsScreen.SettingsCardWithSwitch
import ru.blays.revanced.Elements.Elements.Screens.SettingsScreen.SettingsCheckboxWithTitle
import ru.blays.revanced.Elements.Elements.Screens.SettingsScreen.SettingsExpandableCard
import ru.blays.revanced.Elements.Elements.Screens.SettingsScreen.SettingsRadioButtonWithTitle
import ru.blays.revanced.Services.Root.Util.isRootGranted
import ru.blays.revanced.shared.LogManager.BLog
import ru.blays.revanced.shared.R
import ru.blays.revanced.shared.Util.getStringRes
import ru.hh.toolbar.custom_toolbar.CollapsingTitle
import ru.hh.toolbar.custom_toolbar.CustomToolbar
import ru.hh.toolbar.custom_toolbar.rememberToolbarScrollBehavior
import kotlin.math.round
import kotlin.math.roundToLong

private const val TAG = "SettingsScreen"


class SettingsScreen: AndroidScreen() {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        val dialogNavigator = LocalDialogNavigator.current

        val settingsRepository: SettingsRepository = koinInject()
        val scrollBehavior = rememberToolbarScrollBehavior()

        var isSpinnerExpanded by remember { mutableStateOf(false) }

        val changeSpinnerExpanded = { isSpinnerExpanded = !isSpinnerExpanded }

        val lazyListState = rememberLazyListState()

        shouldHideNavigationBar = when {
            !lazyListState.canScrollForward && lazyListState.canScrollBackward -> true
            !lazyListState.canScrollForward && !lazyListState.canScrollBackward -> false
            else -> false
        }

        Scaffold(
            topBar = {
                CustomToolbar(
                    collapsingTitle = CollapsingTitle.large(titleText = stringResource(R.string.AppBar_Settings)),
                    navigationIcon = {
                        IconButton(onClick = navigator::pop) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = "NavigateBack",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = .8F)
                            )
                        }
                    },
                    actions = {
                        DropdownMenu(expanded = isSpinnerExpanded, onDismissRequest = changeSpinnerExpanded) {
                            DropdownMenuItem(
                                text = { Text(text = stringResource(R.string.About_app)) },
                                onClick = { navigator.push(AboutScreen()) }
                            )
                            DropdownMenuItem(
                                text = { Text(text = stringResource(id = R.string.App_logs)) },
                                onClick = { navigator.push(LogViewerScreen()) }
                            )
                            if (BuildConfig.DEBUG) DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(id = R.string.Crash_app),
                                        color = Color.Red
                                    )
                                },
                                onClick = { throw RuntimeException("Test crash") }
                            )
                        }
                        IconButton(onClick = changeSpinnerExpanded) {
                            Icon(
                                imageVector = Icons.Rounded.MoreVert,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = .8F)
                            )
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
                    .fillMaxSize(),
                state = lazyListState
            ) {
                itemsGroupWithHeader(title = getStringRes(R.string.Settings_title_theme)) {
                    ThemeSelector(repository = settingsRepository)
                    isSAndAboveCompose {
                        MonetColors(repository = settingsRepository)
                    }
                    AmoledTheme(repository = settingsRepository)
                    AccentSelector(
                        openAlertDialog = {
                            dialogNavigator.showNonDismissible(
                                ColorPickerDialog(
                                    color = settingsRepository.currentAccentColor,
                                    onClose = dialogNavigator::hide,
                                    onPick = { color ->
                                        BLog.i(TAG, "selected custom color: $color")
                                        settingsRepository.customAccentColorArgb = color.toArgb()
                                    }
                                )
                            )
                        },
                        settingsRepository.accentColorItem,
                        settingsRepository.isCustomColorSelected,
                        settingsRepository)

                }

                itemsGroupWithHeader(title = getStringRes(R.string.Settings_title_main)) {
                    InstallerType(repository = settingsRepository)
                    ManagedApps(repository = settingsRepository)
                }

                itemsGroupWithHeader(title = getStringRes(id = R.string.Settings_title_cache)) {
                    CacheLifetimeSelector(repository = settingsRepository)
                }
            }
        }
    }
}

@Composable
private fun ThemeSelector(repository: SettingsRepository) {
    SettingsExpandableCard(
        title = stringResource(R.string.Settings_card_theme_title),
        subtitle = stringResource(R.string.Settings_card_theme_description)
    ) {

        val themeCode = repository.appTheme.themeCode!!

        SettingsRadioButtonWithTitle(title = stringResource(R.string.Settings_card_theme_system), checkedIndex = themeCode, index = 0) {
            repository.appTheme = ThemeModel(themeCode = 0)
        }
        SettingsRadioButtonWithTitle(title = stringResource(R.string.Settings_card_theme_dark), checkedIndex = themeCode, index = 1) {
            repository.appTheme = ThemeModel(themeCode = 1)
        }
        SettingsRadioButtonWithTitle(title = stringResource(R.string.Settings_card_theme_light), checkedIndex = themeCode, index = 2) {
            repository.appTheme = ThemeModel(themeCode = 2)
        }
    }
}

@Composable
private fun MonetColors(repository: SettingsRepository) {
    SettingsCardWithSwitch(
        title = stringResource(R.string.Settings_card_monet_title),
        subtitle = stringResource(R.string.Settings_card_monet_description),
        state = repository.monetTheme
    ) {
        repository.monetTheme = it
    }
}

@Composable
fun AmoledTheme(repository: SettingsRepository) {
    SettingsCardWithSwitch(
        title = stringResource(R.string.Settings_card_amoled_title),
        subtitle = stringResource(R.string.Settings_card_amoled_description),
        state = repository.isAmoledTheme
    ) {
        repository.isAmoledTheme = it
    }
}

@Composable
private fun AccentSelector(
    openAlertDialog: () -> Unit,
    selectedItemIndex: Int,
    customColorSelected: Boolean,
    repository: SettingsRepository
) {

    val callback: (Int) -> Unit = { repository.accentColorItem = it }

    val rainbowBrush = Brush.sweepGradient(rainbowColors)

    SettingsExpandableCard(
        title = stringResource(R.string.Settings_card_accent_title),
        subtitle = stringResource(R.string.Settings_card_accent_description)
    ) {
        LazyRow(modifier = Modifier.padding(12.dp)) {
            itemsIndexed(defaultAccentColorsList) { index, item ->
                ColorPickerItem(
                    color = item,
                    index = index,
                    selectedItemIndex = if (customColorSelected) null else selectedItemIndex,
                    actionSelectColor = callback
                )
            }
            item {
                ColorPickerItem(
                    brush = rainbowBrush,
                    customColorSelected = customColorSelected,
                    actionSelect = { repository.isCustomColorSelected = true },
                    actionOpenDialog = openAlertDialog
                )
            }
        }
    }
}


@Composable
fun InstallerType(repository: SettingsRepository) {
    SettingsExpandableCard(
        title = stringResource(R.string.Settings_card_installer_title),
        subtitle = stringResource(R.string.Settings_card_installer_description)
    ) {
        SettingsRadioButtonWithTitle(
            title = stringResource(R.string.Settings_card_installer_session),
            checkedIndex = repository.installerType,
            index = 1
        ) {
            repository.installerType = 1
        }
        SettingsRadioButtonWithTitle(
            title =  stringResource(R.string.Settings_card_installer_root),
            checkedIndex = repository.installerType,
            index = 2,
            enabled = isRootGranted
        ) {
            repository.installerType = 2
        }
        SettingsRadioButtonWithTitle(
            title =  stringResource(R.string.Settings_card_installer_shizuku),
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
        title = stringResource(R.string.Settings_card_selected_apps_title),
        subtitle = stringResource(R.string.Settings_card_selected_apps_description)
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

@Composable
fun CacheLifetimeSelector(repository: SettingsRepository) {

    val segments  = arrayOf(
        Segment(0F, stringResource(R.string.SeekBar_segment_3_hours)),
        Segment(2F, stringResource(R.string.SeekBar_segment_6_hours)),
        Segment(4F, stringResource(R.string.SeekBar_segment_12_hours)),
        Segment(6F, stringResource(R.string.SeekBar_segment_24_hours)),
        Segment(8F, stringResource(R.string.SeekBar_segment_48_hours)),
        Segment(10F, stringResource(R.string.SeekBar_segment_infinity))
    )

    val segmentsRange = remember {
        segments.first().start..segments.last().start
    }

    val steps = segments.size - 2

    var currentSegment by remember {
        mutableStateOf(Segment(0F, ""))
    }

    LaunchedEffect(Unit) {
        try {
            currentSegment = segments.first { it.start == repository.cacheLifetimeLong.toFloat() }
        } catch (_: Exception) {}
    }

    SettingsExpandableCard(
        title = stringResource(id = R.string.Settings_card_cache_lifetime_title),
        subtitle = stringResource(id = R.string.Settings_card_cache_lifetime_description)
    ) {

        Spacer(modifier = Modifier.height(8.dp))

        CurrentSegment(
            currentSegment = currentSegment,
            alignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Slider(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp),
            value = repository.cacheLifetimeLong.toFloat(),
            valueRange = segmentsRange,
            steps = steps,
            onValueChange = { newValue ->
                try {
                    currentSegment = segments.first { it.start == round(newValue) }
                } catch (_: Exception) {}
                repository.cacheLifetimeLong = newValue.roundToLong()
            }
        )
    }
}

class ColorPickerDialog(
    private val color: Color,
    private val onClose: () -> Unit,
    private val onPick: (color: Color) -> Unit
): Screen {
    @Composable
    override fun Content() {
        ColorPickerDialogContent(
            color = color,
            onClose = onClose,
            onPick = onPick
        )
    }
}