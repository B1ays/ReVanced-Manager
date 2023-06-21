package ru.blays.revanced.shared.Extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

fun <T> CoroutineScope.collect(flow: Flow<T>, block: suspend (T) -> Unit) {
    val collector = FlowCollector(block)
    launch {
        flow.collect(collector)
    }
}