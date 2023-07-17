package ru.blays.helios.core

import java.util.concurrent.ConcurrentHashMap

class ThreadSafeMap<K, V> : MutableMap<K, V> by ConcurrentHashMap()
