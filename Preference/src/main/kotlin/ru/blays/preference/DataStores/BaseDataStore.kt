package ru.blays.preference.DataStores

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.blays.preference.Interfaces.IDataStore
import ru.blays.preference.dataStore
import kotlin.reflect.KProperty

abstract class BaseDataStore <T: Any> internal constructor (context: Context): IDataStore<T> {

    final override val dataStore: DataStore<Preferences> = context.dataStore

    final override val coroutineContext = Dispatchers.IO

    val flow: Flow<T> = dataStore.data.map { preferences ->
        preferences[KEY] ?: DEFAULT_VALUE
    }

    var value: T
        get() = runBlocking { flow.first() }
        set(value) {
            launch {
                dataStore.edit { mutablePreferences ->
                    mutablePreferences[KEY] = value
                }
            }
        }

    @NonRestartableComposable
    @Composable
    fun asState(): State<T> {
        return flow.collectAsState(DEFAULT_VALUE, context = coroutineContext)
    }

    @Suppress("NOTHING_TO_INLINE")
    inline operator fun getValue(thisObj: Any?, property: KProperty<*>): T = runBlocking { flow.first() }

    @Suppress("NOTHING_TO_INLINE")
    inline operator fun setValue(thisObj: Any?, property: KProperty<*>, value: T) {
        launch {
            dataStore.edit { mutablePreferences ->
                mutablePreferences[KEY] = value
            }
        }
    }
}