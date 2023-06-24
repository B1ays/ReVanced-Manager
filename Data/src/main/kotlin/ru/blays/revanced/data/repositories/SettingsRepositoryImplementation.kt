package ru.blays.revanced.data.repositories

import android.content.Context
import android.content.SharedPreferences

private const val APP_THEME_TYPE = "AppTheme"
private const val APP_THEME_MONET = "MonetTheme"
private const val AMOLED_THEME = "AmoledTheme"
private const val APP_THEME_ACCENT = "AccentColor"
private const val ROOT_MODE = "RootMode"
private const val INSTALLER_TYPE = "InstallerType"

private const val MANAGED_YOUTUBE = "youtubeManaging"
private const val MANAGED_MUSIC = "musicManaging"
private const val MANAGED_MICROG = "microGManaging"

private const val CACHE_LIFETIME_LONG = "CacheLifetimeLong"

class SettingsRepositoryImplementation(context: Context) {

    private val preferences: SharedPreferences = context.getSharedPreferences("App", Context.MODE_PRIVATE)

    private inline fun <reified T : Any> putSetting(key: String, value: T) {
        when (T::class) {
            String::class -> preferences.edit().putString(key, (value as String)).apply()
            Boolean::class -> preferences.edit().putBoolean(key, (value as Boolean)).apply()
            Int::class -> preferences.edit().putInt(key, (value as Int)).apply()
            Long::class -> preferences.edit().putLong(key, (value as Long)).apply()
            Float::class -> preferences.edit().putFloat(key, (value as Float)).apply()
        }
    }

    private inline fun <reified T: Any> getSetting(key: String, defValue: T) : T? {
        return when(T::class) {
            String::class -> preferences.getString(key, (defValue as String)) as T
            Boolean::class -> preferences.getBoolean(key, (defValue as Boolean)) as T
            Int::class -> preferences.getInt(key, (defValue as Int)) as T
            Long::class -> preferences.getLong(key, (defValue as Long)) as T
            Float::class -> preferences.getFloat(key, (defValue as Float)) as T
            else -> null
        }
    }

    var theme: Int
        get() = getSetting(APP_THEME_TYPE, 0)!!
        set(value) = putSetting(APP_THEME_TYPE, value)

    var monetTheme: Boolean
        get() = getSetting(APP_THEME_MONET, true)!!
        set(value) = putSetting(APP_THEME_MONET, value)

    var isAmoledTheme: Boolean
        get() = getSetting(AMOLED_THEME, false)!!
        set(value) = putSetting(AMOLED_THEME, value)

    var accentColor: Int
        get() = getSetting(APP_THEME_ACCENT, 1)!!
        set(value) = putSetting(APP_THEME_ACCENT, value)

    var isRootMode: Boolean
        get() = getSetting(ROOT_MODE, false)!!
        set(value) = putSetting(ROOT_MODE, value)

    var installerType: Int
        get() = getSetting(INSTALLER_TYPE, 1)!!
        set(value) = putSetting(INSTALLER_TYPE, value)

    var youtubeManaged: Boolean
        get() = getSetting(MANAGED_YOUTUBE, true)!!
        set(value) = putSetting(MANAGED_YOUTUBE, value)

    var musicManaged: Boolean
        get() = getSetting(MANAGED_MUSIC, false)!!
        set(value) = putSetting(MANAGED_MUSIC, value)

    var microGManaged: Boolean
        get() = getSetting(MANAGED_MICROG, false)!!
        set(value) = putSetting(MANAGED_MICROG, value)

    var cacheLifetimeLong: Long
        get() = getSetting(CACHE_LIFETIME_LONG, 2)!!
        set(value) = putSetting(CACHE_LIFETIME_LONG, value)

    val cacheLifetimeReal: Long = when(cacheLifetimeLong) {
        0L -> 3L
        2L -> 6L
        4L -> 12L
        6L -> 24L
        8L -> 48L
        10L -> Int.MAX_VALUE.toLong()
        else -> 6L
    }
}