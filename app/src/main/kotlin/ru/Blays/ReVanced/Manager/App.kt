package ru.Blays.ReVanced.Manager

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationManagerCompat
import com.topjohnwu.superuser.Shell
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.Blays.ReVanced.Manager.BackgroundService.CrashHandlerService
import ru.Blays.ReVanced.Manager.DI.appModule
import ru.blays.revanced.data.DI.dataModule
import ru.blays.revanced.shared.LogManager.Data.BLog
import ru.blays.revanced.shared.R
import ru.blays.revanced.shared.Util.getStringRes

private const val TAG = "Application"

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        BLog.i(TAG, "onCreate app")

        // Create LibSu Shell
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(20)
        )

        BLog.i(TAG, "Start koin")

        // Start koin DI
        startKoin {
            androidContext(this@App)
            modules(appModule)
            modules(dataModule)
        }

        // create channel for notifications
        val channel = NotificationChannel(
            "update",
            getStringRes(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Update checker"
        }
        NotificationManagerCompat.from(this@App).createNotificationChannel(channel)

        // Crash handler
        Thread.setDefaultUncaughtExceptionHandler(CrashHandlerService(this))
    }
}