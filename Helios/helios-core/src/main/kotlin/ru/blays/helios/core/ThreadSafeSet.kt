package ru.blays.helios.core

import java.util.concurrent.CopyOnWriteArraySet

class ThreadSafeSet<T> : MutableSet<T> by CopyOnWriteArraySet()
