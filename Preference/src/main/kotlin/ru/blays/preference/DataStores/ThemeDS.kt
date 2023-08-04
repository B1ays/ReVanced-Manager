package ru.blays.preference.DataStores

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey

class ThemeDS internal constructor(context: Context): BaseDataStore<Int>(context) {
    override val KEY: Preferences.Key<Int> = intPreferencesKey("appTheme")
    override val DEFAULT_VALUE: Int = 0
}