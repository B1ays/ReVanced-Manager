package ru.blays.preference.DataStores

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey

class DownloadsFolderUriDS(context: Context): BaseDataStore<String>(context) {
    override val KEY: Preferences.Key<String> = stringPreferencesKey("downloadsFolderUri")
    override val DEFAULT_VALUE: String = ""
}