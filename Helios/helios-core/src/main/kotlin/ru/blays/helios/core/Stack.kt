package ru.blays.helios.core

import kotlin.reflect.KClass

inline fun <reified I : Item, Item> Stack<Item>.popUntil(): Boolean =
    popUntil { item -> item is I }

fun <T: Screen> Stack<Screen>.popUntil(targetScreen: KClass<T>): Boolean =
    popUntil { screen -> screen == targetScreen }


enum class StackEvent {
    Push,
    Replace,
    Pop,
    Idle
}

interface Stack<Item> {

    val items: List<Item>

    val lastEvent: StackEvent

    val lastItemOrNull: Item?

    val size: Int

    val isEmpty: Boolean

    val canPop: Boolean

    infix fun push(item: Item)

    infix fun push(items: List<Item>)

    infix fun replace(item: Item)

    infix fun replaceAll(item: Item)

    infix fun replaceAll(items: List<Item>)

    fun pop(): Boolean

    fun popAll()

    infix fun popUntil(predicate: (Item) -> Boolean): Boolean

    operator fun plusAssign(item: Item)

    operator fun plusAssign(items: List<Item>)

    fun clearEvent()
}
