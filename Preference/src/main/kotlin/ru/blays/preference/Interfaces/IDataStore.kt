package ru.blays.preference.Interfaces

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope

@Suppress("PropertyName")
interface IDataStore <T: Any>: CoroutineScope {
    val dataStore: DataStore<Preferences>

    val KEY: Preferences.Key<T>

    val DEFAULT_VALUE: T

}