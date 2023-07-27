package ru.blays.preference

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

internal val Context.dataStore by preferencesDataStore(
    name = "Settings"
)
