package ru.blays.revanced.data.repositories

import android.content.Context
import android.content.SharedPreferences

private const val APP_THEME_TYPE = "AppTheme"
private const val APP_THEME_MONET = "MonetTheme"
private const val APP_THEME_ACCENT = "AccentColor"

class SettingsRepositoryImplementation(context: Context) {

    private val preferences: SharedPreferences = context.getSharedPreferences("App", Context.MODE_PRIVATE)

    var theme: Int
        get() = preferences.getInt(APP_THEME_TYPE, 0)
        set(value) = preferences.edit().putInt(APP_THEME_TYPE, value).apply()

    var monetTheme: Boolean
        get() = preferences.getBoolean(APP_THEME_MONET, true)
        set(value) = preferences.edit().putBoolean(APP_THEME_MONET, value).apply()

    var accentColor: Int
        get() = preferences.getInt(APP_THEME_ACCENT, 1)
        set(value) = preferences.edit().putInt(APP_THEME_ACCENT, value).apply()

}