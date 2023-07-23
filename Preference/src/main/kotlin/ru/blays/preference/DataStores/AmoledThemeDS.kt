package ru.blays.preference.DataStores

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey

class AmoledThemeDS(context: Context): BaseDataStore<Boolean>(context) {
    override val KEY: Preferences.Key<Boolean> = booleanPreferencesKey("amoledTheme")
    override val DEFAULT_VALUE: Boolean = false
}