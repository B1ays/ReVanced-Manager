package ru.blays.revanced.Elements.Repository



import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.blays.revanced.Elements.DataClasses.AccentColorItem
import ru.blays.revanced.Elements.Util.BuildedTheme
import ru.blays.revanced.Elements.Util.buildTheme
import ru.blays.revanced.data.repositories.SettingsRepositoryImplementation

class SettingsRepository(private val settingsRepositoryImplementation: SettingsRepositoryImplementation) {

    private var _appThemeCode by mutableStateOf(0)

    private var _appTheme by mutableStateOf(true)

    private var _monetTheme by mutableStateOf(true)

    private var _accentColorItem by mutableStateOf(1)

    private var _isRootMode by mutableStateOf(false)

    private var _installerType by mutableStateOf(0)

    private var _youtubeManaged by mutableStateOf(true)

    private var _musicManaged by mutableStateOf(false)

    private var _microGManaged by mutableStateOf(false)

    var buildedTheme: MutableState<BuildedTheme>
        private set

    var isSystemInDarkMode = true

    init {

        _appThemeCode = settingsRepositoryImplementation.theme

        _appTheme = when(settingsRepositoryImplementation.theme) {
            0 -> isSystemInDarkMode
            1 -> true
            2 -> false
            else -> true
        }

        _monetTheme = settingsRepositoryImplementation.monetTheme

        _accentColorItem = settingsRepositoryImplementation.accentColor

        _isRootMode = settingsRepositoryImplementation.isRootMode

        _installerType = settingsRepositoryImplementation.installerType

        _youtubeManaged = settingsRepositoryImplementation.youtubeManaged

        _musicManaged = settingsRepositoryImplementation.musicManaged

        _microGManaged = settingsRepositoryImplementation.microGManaged

        buildedTheme = mutableStateOf(generateTheme(_accentColorItem))
    }

    var appTheme: ThemeModel
        get() = ThemeModel(isDarkMode = _appTheme, themeCode = _appThemeCode)
        set(value) {
            _appTheme = when(value.themeCode) {
                0 -> isSystemInDarkMode
                1 -> true
                2 -> false
                else -> isSystemInDarkMode
            }
            if (value.themeCode != null) {
                _appThemeCode = value.themeCode
                settingsRepositoryImplementation.theme = value.themeCode
            }
        }

    var monetTheme: Boolean
        get() = _monetTheme
        set(value) {
            _monetTheme = value
            settingsRepositoryImplementation.monetTheme = value
        }

    var accentColorItem: Int
        get() = _accentColorItem
        set(value) {
            buildedTheme.value = generateTheme(value)
            _accentColorItem = value
            settingsRepositoryImplementation.accentColor = value
        }

    var isRootMode: Boolean
        get() = _isRootMode
        set(value) {
            _isRootMode = value
            settingsRepositoryImplementation.isRootMode = value
        }

    var installerType: Int
        get() = _installerType
        set(value) {
            _installerType = value
            settingsRepositoryImplementation.installerType = value
        }

    var youtubeManaged: Boolean
        get() = _youtubeManaged
        set(value) {
            _youtubeManaged = value
            settingsRepositoryImplementation.youtubeManaged = value
        }

    var musicManaged: Boolean
        get() = _musicManaged
        set(value) {
            _musicManaged = value
            settingsRepositoryImplementation.musicManaged = value
        }

    var microGManaged: Boolean
        get() = _microGManaged
        set(value) {
            _microGManaged = value
            settingsRepositoryImplementation.microGManaged = value
        }


    private fun generateTheme(index: Int) : BuildedTheme =  with(
        AccentColorItem.list[if (index in AccentColorItem.list.indices) index else 1]
    ) {
        buildTheme(accentDark, accentLight)
    }

}

data class ThemeModel(
    val isDarkMode: Boolean? = null,
    val themeCode: Int? = null
)