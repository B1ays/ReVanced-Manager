package ru.blays.preference.DataStores

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey

class CustomColorValueDS internal constructor(context: Context): BaseDataStore<Int>(context) {
    override val KEY: Preferences.Key<Int> = intPreferencesKey("CustomColorARGB")
    override val DEFAULT_VALUE: Int = 0
}