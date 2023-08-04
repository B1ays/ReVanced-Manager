package ru.blays.revanced.DeviceUtils.NonRoot.Services

import android.app.Service
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Build
import android.os.IBinder
import ru.blays.revanced.DeviceUtils.NonRoot.PackageManager.installerStatusFlow

internal class AppInstallService: Service() {

    @Suppress("DEPRECATION")
    override fun onStartCommand(
        intent: Intent,
        flags: Int,
        startId: Int
    ): Int {
        val extraStatus = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, -999)
        val extraStatusMessage = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)
        val extraSessionID = intent.getIntExtra(PackageInstaller.EXTRA_SESSION_ID, -1)
        when (extraStatus) {
            PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                startActivity(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(Intent.EXTRA_INTENT, Intent::class.java).apply {
                            this?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                    } else {
                        intent.getParcelableExtra<Intent>(Intent.EXTRA_INTENT).apply {
                            this?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                    }
                )
            }
            else -> {
                installerStatusFlow.tryEmit(extraSessionID to extraStatus)
            }
        }
        stopSelf()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val APP_INSTALL_ACTION = "APP_INSTALL_ACTION"

        const val EXTRA_INSTALL_STATUS = "EXTRA_INSTALL_STATUS"
        const val EXTRA_INSTALL_STATUS_MESSAGE = "EXTRA_INSTALL_STATUS_MESSAGE"
    }

}