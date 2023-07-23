@file:Suppress("LocalVariableName")

package ru.Blays.ReVanced.Manager.UI.Screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import ru.Blays.ReVanced.Manager.BuildConfig
import ru.Blays.ReVanced.Manager.Data.defaultAccentColorsList
import ru.Blays.ReVanced.Manager.UI.Navigation.shouldHideNavigationBar
import ru.Blays.ReVanced.Manager.UI.Theme.rainbowColors
import ru.Blays.ReVanced.Manager.Utils.isSAndAboveCompose
import ru.blays.helios.androidx.AndroidScreen
import ru.blays.helios.core.Screen
import ru.blays.helios.dialogs.LocalDialogNavigator
import ru.blays.helios.navigator.LocalNavigator
import ru.blays.helios.navigator.currentOrThrow
import ru.blays.preference.DataStores.AmoledThemeDS
import ru.blays.preference.DataStores.CacheLifetimeDS
import ru.blays.preference.DataStores.ColorAccentIndexDS
import ru.blays.preference.DataStores.CustomColorSelectedDS
import ru.blays.preference.DataStores.CustomColorValueDS
import ru.blays.preference.DataStores.DownloadsFolderUriDS
import ru.blays.preference.DataStores.InstallerTypeDS
import ru.blays.preference.DataStores.MonetColorsDS
import ru.blays.preference.DataStores.StorageAccessTypeDS
import ru.blays.preference.DataStores.ThemeDS
import ru.blays.revanced.Elements.DataClasses.DefaultPadding
import ru.blays.revanced.Elements.Elements.LazyItems.itemsGroupWithHeader
import ru.blays.revanced.Elements.Elements.Screens.SettingsScreen.ColorPickerDialogContent
import ru.blays.revanced.Elements.Elements.Screens.SettingsScreen.ColorPickerItem
import ru.blays.revanced.Elements.Elements.Screens.SettingsScreen.CurrentSegment
import ru.blays.revanced.Elements.Elements.Screens.SettingsScreen.Segment
import ru.blays.revanced.Elements.Elements.Screens.SettingsScreen.SettingsCardWithSwitch
import ru.blays.revanced.Elements.Elements.Screens.SettingsScreen.SettingsExpandableCard
import ru.blays.revanced.Elements.Elements.Screens.SettingsScreen.SettingsRadioButtonWithTitle
import ru.blays.revanced.Services.Root.Util.isRootGranted
import ru.blays.revanced.data.Downloader.Utils.DEFAULT_DOWNLOADS_FOLDER
import ru.blays.revanced.shared.Extensions.getFileUri
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

        val scrollBehavior = rememberToolbarScrollBehavior()

        var isSpinnerExpanded by remember { mutableStateOf(false) }

        val changeSpinnerExpanded = { isSpinnerExpanded = !isSpinnerExpanded }

        val lazyListState = rememberLazyListState()

        val context = LocalContext.current

        val customColorValueDS = remember {
            CustomColorValueDS(context)
        }
        var customColorValue by customColorValueDS

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
                    ThemeSelector()
                    isSAndAboveCompose {
                        MonetColors()
                    }
                    AmoledTheme()
                    AccentSelector(
                        openAlertDialog = {
                            dialogNavigator.showNonDismissible(
                                ColorPickerDialog(
                                    color = Color(customColorValue),
                                    onClose = dialogNavigator::hide,
                                    onPick = { color ->
                                        BLog.i(TAG, "selected custom color: $color")
                                        customColorValue = color.toArgb()
                                    }
                                )
                            )
                        }
                    )
                }

                itemsGroupWithHeader(title = getStringRes(R.string.Settings_title_main)) {
                    InstallerType()
                    ManagedApps()
                    DownloadsFolderSelector()
                }

                itemsGroupWithHeader(title = getStringRes(id = R.string.Settings_title_cache)) {
                    CacheLifetimeSelector()
                }
            }
        }
    }
}

@Composable
private fun ThemeSelector() {
    SettingsExpandableCard(
        title = stringResource(R.string.Settings_card_theme_title),
        subtitle = stringResource(R.string.Settings_card_theme_description)
    ) {
        val context = LocalContext.current
        val themeDS: ThemeDS = remember {
            ThemeDS(context)
        }
        var checkedIndex by themeDS
        val checkedIndexState by themeDS.asState()

        SettingsRadioButtonWithTitle(
            title = stringResource(R.string.Settings_card_theme_system),
            checkedIndex = checkedIndexState,
            index = 0
        ) {
           checkedIndex = 0
        }
        SettingsRadioButtonWithTitle(
            title = stringResource(R.string.Settings_card_theme_dark),
            checkedIndex = checkedIndexState,
            index = 1
        ) {
            checkedIndex = 1
        }
        SettingsRadioButtonWithTitle(
            title = stringResource(R.string.Settings_card_theme_light),
            checkedIndex = checkedIndexState,
            index = 2
        ) {
            checkedIndex = 2
        }
    }
}

@Composable
private fun MonetColors() {
    val context = LocalContext.current
    val monetColorAccentDS = remember {
        MonetColorsDS(context)
    }
    var monetColorAccent by monetColorAccentDS
    val state by monetColorAccentDS.asState()

    SettingsCardWithSwitch(
        title = stringResource(R.string.Settings_card_monet_title),
        subtitle = stringResource(R.string.Settings_card_monet_description),
        state = state
    ) { value ->
        monetColorAccent = value
    }
}

@Composable
fun AmoledTheme() {
    val context = LocalContext.current
    val amoledThemeDS = remember {
        AmoledThemeDS(context)
    }
    var amoledTheme by amoledThemeDS
    val state by amoledThemeDS.asState()

    SettingsCardWithSwitch(
        title = stringResource(R.string.Settings_card_amoled_title),
        subtitle = stringResource(R.string.Settings_card_amoled_description),
        state = state
    ) { value ->
        amoledTheme = value
    }
}

@Composable
private fun AccentSelector(
    openAlertDialog: () -> Unit
) {
    val context = LocalContext.current
    val colorAccentIndexDS = remember {
        ColorAccentIndexDS(context)
    }
    var colorAccent by colorAccentIndexDS
    val colorAccentFlow by colorAccentIndexDS.asState()

    val customColorSelectedDS = remember {
        CustomColorSelectedDS(context)
    }
    var customColorSelected by customColorSelectedDS
    val customColorSelectedFlow by customColorSelectedDS.asState()


    val rainbowBrush = Brush.sweepGradient(rainbowColors)

    val actionSelectColor: (Int) -> Unit = { index ->
        colorAccent = index
        customColorSelected = false
    }

    SettingsExpandableCard(
        title = stringResource(R.string.Settings_card_accent_title),
        subtitle = stringResource(R.string.Settings_card_accent_description)
    ) {
        LazyRow(modifier = Modifier.padding(12.dp)) {
            itemsIndexed(defaultAccentColorsList) { index, item ->
                ColorPickerItem(
                    color = item,
                    index = index,
                    selectedItemIndex = if (customColorSelectedFlow) null else colorAccentFlow,
                    actionSelectColor = actionSelectColor
                )
            }
            item {
                ColorPickerItem(
                    brush = rainbowBrush,
                    customColorSelected = customColorSelectedFlow,
                    actionSelect = { customColorSelected = true },
                    actionOpenDialog = openAlertDialog
                )
            }
        }
    }
}


@Composable
fun InstallerType() {
    val context = LocalContext.current
    val dataStore = remember {
        InstallerTypeDS(context)
    }
    var installerType by dataStore
    val installerTypeState by dataStore.asState()

    SettingsExpandableCard(
        title = stringResource(R.string.Settings_card_installer_title),
        subtitle = stringResource(R.string.Settings_card_installer_description)
    ) {
        SettingsRadioButtonWithTitle(
            title = stringResource(R.string.Settings_card_installer_session),
            checkedIndex = installerTypeState,
            index = 1
        ) {
            installerType = 1
        }
        SettingsRadioButtonWithTitle(
            title =  stringResource(R.string.Settings_card_installer_root),
            checkedIndex = installerTypeState,
            index = 2,
            enabled = isRootGranted
        ) {
            installerType = 2
        }
        SettingsRadioButtonWithTitle(
            title =  stringResource(R.string.Settings_card_installer_shizuku),
            checkedIndex = installerTypeState,
            index = 3,
            enabled = false
        ) {
            installerType = 3
        }
    }
}

@Composable
fun ManagedApps() {
    /*val context = LocalContext.current
    val dataStore = remember {
        InstallerTypeDS(context)
    }
    val _state by dataStore
    val state by _state.collectAsState()*/

    /*SettingsExpandableCard(
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
    }*/
}

@Composable
fun CacheLifetimeSelector() {

    val context = LocalContext.current
    val cacheDS = CacheLifetimeDS(context)
    var cacheLifetime by cacheDS
    val cacheLifetimeState by cacheDS.asState()

    val segments = remember {
        arrayOf(
            Segment(0F, getStringRes(R.string.SeekBar_segment_3_hours)),
            Segment(2F, getStringRes(R.string.SeekBar_segment_6_hours)),
            Segment(4F, getStringRes(R.string.SeekBar_segment_12_hours)),
            Segment(6F, getStringRes(R.string.SeekBar_segment_24_hours)),
            Segment(8F, getStringRes(R.string.SeekBar_segment_48_hours)),
            Segment(10F, getStringRes(R.string.SeekBar_segment_infinity))
        )
    }

    val segmentsRange = remember {
        segments.first().start..segments.last().start
    }

    val steps = remember {
        segments.size - 2
    }

    var currentSegment by remember {
        mutableStateOf(Segment(0F, ""))
    }

    LaunchedEffect(Unit) {
        try {
            currentSegment = segments.first { it.start == cacheLifetime.toFloat() }
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
            value = cacheLifetimeState.toFloat(),
            valueRange = segmentsRange,
            steps = steps,
            onValueChange = { newValue ->
                try {
                    currentSegment = segments.first { it.start == round(newValue) }
                } catch (_: Exception) {}
                cacheLifetime = newValue.roundToLong()
            }
        )
    }
}

@Composable
fun DownloadsFolderSelector() {
    val context = LocalContext.current
    val storageAccessDS = remember {
        StorageAccessTypeDS(context)
    }
    val downloadsFolderUriDS = remember {
        DownloadsFolderUriDS(context)
    }
    var storageAccessType by storageAccessDS
    val storageAccessTypeState by storageAccessDS.asState()
    var downloadsFolderUri by downloadsFolderUriDS
    val downloadsFolderUriFlow by downloadsFolderUriDS.asState()
    val register = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()) { uriOrNull ->
        uriOrNull?.let { uri ->
            downloadsFolderUri = uri.toString()
        }
    }
    val defaultDownloadFolderUri = context.getFileUri(DEFAULT_DOWNLOADS_FOLDER)
    SettingsExpandableCard(title = "DownloadFolder") {
        SettingsRadioButtonWithTitle(
            title = "Default",
            checkedIndex = storageAccessTypeState,
            index = 0
        ) {
            storageAccessType = 0
        }
        SettingsRadioButtonWithTitle(
            title = "From SAF",
            checkedIndex = storageAccessTypeState,
            index = 1
        ) {
            storageAccessType = 1

        }
        Text(
            modifier = Modifier
                .padding(DefaultPadding.CardDefaultPadding),
            text = "Select folder: ${downloadsFolderUriFlow.toUri().path}"
        )
        Button(
            modifier = Modifier
                .defaultMinSize(minHeight = 50.dp)
                .padding(DefaultPadding.CardDefaultPadding)
                .fillMaxWidth(),
            onClick = { register.launch(defaultDownloadFolderUri) },
            enabled = storageAccessType == 1,
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = "Select folder",
                style = MaterialTheme.typography.titleMedium
            )
        }
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