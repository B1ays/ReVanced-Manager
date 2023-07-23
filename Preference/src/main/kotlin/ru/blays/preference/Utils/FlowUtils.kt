package ru.blays.preference.Utils


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal fun <T> Flow<T>.asMutableStateFlow(
    scope: CoroutineScope,
    initialValue: T
): MutableStateFlow<T> {
    val flow = MutableStateFlow(initialValue)
    scope.launch {
        this@asMutableStateFlow.collect(flow)
    }
    return flow
}