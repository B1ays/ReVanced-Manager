package ru.Blays.ReVanced.Manager.Utils.ModuleInstallerLogAdapter

import ru.blays.revanced.DeviceUtils.Root.ModuleIntstaller.LogAdapter.LogAdapter
import ru.blays.revanced.shared.LogManager.BLog

class ModuleInstallerLogAdapter: LogAdapter {
    override fun d(message: String) {
        BLog.d(TAG, message)
    }

    override fun i(message: String) {
        BLog.i(TAG, message)
    }

    override fun w(message: String) {
        BLog.w(TAG, message)
    }

    override fun e(message: String) {
        BLog.e(TAG, message)
    }
}