package ru.blays.preference.DataStores

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey

class CacheLifetimeDS(context: Context): BaseDataStore<Long>(context) {
    override val KEY: Preferences.Key<Long> = longPreferencesKey("CacheLifetimeLong")
    override val DEFAULT_VALUE: Long = 6L

}