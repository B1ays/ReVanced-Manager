package ru.Blays.ReVanced.Manager.Repository



import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.blays.revanced.Elements.DataClasses.AccentColorItem
import ru.blays.revanced.Elements.Util.BuildedTheme
import ru.blays.revanced.Elements.Util.buildTheme
import ru.blays.revanced.data.repositories.SettingsRepositoryImplementation

class SettingsRepository(private val settingsRepositoryImpl: SettingsRepositoryImplementation) {

    private var _appThemeCode by mutableIntStateOf(0)

    private var _appTheme by mutableStateOf(true)

    private var _monetTheme by mutableStateOf(true)

    private var _isAmoledTheme by mutableStateOf(false)

    private var _accentColorItem by mutableIntStateOf(1)

    private var _isRootMode by mutableStateOf(false)

    private var _installerType by mutableIntStateOf(0)

    private var _youtubeManaged by mutableStateOf(true)

    private var _musicManaged by mutableStateOf(false)

    private var _microGManaged by mutableStateOf(false)

    private var _cacheLifetimeLong by mutableLongStateOf(0)

    var buildedTheme: MutableState<BuildedTheme>
        private set

    var isSystemInDarkMode = true

    init {

        _appThemeCode = settingsRepositoryImpl.theme

        _appTheme = when(settingsRepositoryImpl.theme) {
            0 -> isSystemInDarkMode
            1 -> true
            2 -> false
            else -> true
        }

        _monetTheme = settingsRepositoryImpl.monetTheme

        _accentColorItem = settingsRepositoryImpl.accentColor

        _isAmoledTheme = settingsRepositoryImpl.isAmoledTheme

        _isRootMode = settingsRepositoryImpl.isRootMode

        _installerType = settingsRepositoryImpl.installerType

        _youtubeManaged = settingsRepositoryImpl.youtubeManaged

        _musicManaged = settingsRepositoryImpl.musicManaged

        _microGManaged = settingsRepositoryImpl.microGManaged

        _cacheLifetimeLong = settingsRepositoryImpl.cacheLifetimeLong

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
                settingsRepositoryImpl.theme = value.themeCode
            }
        }

    var monetTheme: Boolean
        get() = _monetTheme
        set(value) {
            _monetTheme = value
            settingsRepositoryImpl.monetTheme = value
        }

    var isAmoledTheme: Boolean
        get() = _isAmoledTheme
        set(value) {
            _isAmoledTheme = value
            settingsRepositoryImpl.isAmoledTheme = value
        }


    var accentColorItem: Int
        get() = _accentColorItem
        set(value) {
            buildedTheme.value = generateTheme(value)
            _accentColorItem = value
            settingsRepositoryImpl.accentColor = value
        }

    var isRootMode: Boolean
        get() = _isRootMode
        set(value) {
            _isRootMode = value
            settingsRepositoryImpl.isRootMode = value
        }

    var installerType: Int
        get() = _installerType
        set(value) {
            _installerType = value
            settingsRepositoryImpl.installerType = value
        }

    var youtubeManaged: Boolean
        get() = _youtubeManaged
        set(value) {
            _youtubeManaged = value
            settingsRepositoryImpl.youtubeManaged = value
        }

    var musicManaged: Boolean
        get() = _musicManaged
        set(value) {
            _musicManaged = value
            settingsRepositoryImpl.musicManaged = value
        }

    var microGManaged: Boolean
        get() = _microGManaged
        set(value) {
            _microGManaged = value
            settingsRepositoryImpl.microGManaged = value
        }

    var cacheLifetimeLong: Long
        get() = _cacheLifetimeLong
        set(value) {
            _cacheLifetimeLong = value
            settingsRepositoryImpl.cacheLifetimeLong = value
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