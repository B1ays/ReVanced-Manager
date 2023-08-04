package ru.blays.revanced.DeviceUtils.Root.ModuleIntstaller.LogAdapter

interface LogAdapter {

    val TAG: String
        get() = "ModuleInstaller"

    fun d(message: String)

    fun i(message: String)

    fun w(message: String)

    fun e(message: String)
}