package ru.Blays.ReVanced.Manager

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationManagerCompat
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.Blays.ReVanced.Manager.BackgroundService.updateCheckService
import ru.Blays.ReVanced.Manager.DI.appModule
import ru.blays.revanced.Elements.Util.getStringRes
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        // Create LibSu Shell
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(20)
        )

        // Start koin DI
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }

        // create channel for notifications
        val channel = NotificationChannel("update", getStringRes(R.string.notification_channel_name), NotificationManager
            .IMPORTANCE_DEFAULT).apply {
            description = "Update checker"
        }
        NotificationManagerCompat.from(this@App).createNotificationChannel(channel)

        // Launch update check service
        val service = Executors.newSingleThreadScheduledExecutor()
        val handler = Handler(Looper.getMainLooper())
        service.scheduleAtFixedRate({
            handler.run {
                CoroutineScope(Dispatchers.Default).launch { updateCheckService(this@App) }
            }
        }, 0, 6, TimeUnit.HOURS)
    }
}