package ru.blays.preference.DataStores

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey

class InstallerTypeDS internal constructor(context: Context): BaseDataStore<Int>(context) {
    override val KEY: Preferences.Key<Int> = intPreferencesKey("installer type")
    override val DEFAULT_VALUE: Int = 0
}