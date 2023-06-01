package ru.Blays.ReVanced.Manager.BackgroundService

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.Blays.ReVanced.Manager.Data.Apps
import ru.Blays.ReVanced.Manager.R
import ru.blays.revanced.Elements.Util.getStringRes
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

private val createBuilder: (context: Context, message: String) -> NotificationCompat.Builder
    get() = { context, message ->
        NotificationCompat.Builder(context, "update")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getStringRes(R.string.notification_title))
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }

@SuppressLint("MissingPermission")
private val showNotification: (context: Context, builder: NotificationCompat.Builder) -> Unit = { context, builder ->
    with(NotificationManagerCompat.from(context)) {
        if (
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED ||
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        ) {
            notify((0..1000000).random(), builder.build())
        }
    }
}

private fun executeAfterDelay(delay: Duration, block: () -> Unit): Job {
    return CoroutineScope(Dispatchers.Default).launch {
        delay(delay)
        block()
    }
}

private val DELAY = 1.minutes

@OptIn(ExperimentalStdlibApi::class)
suspend fun updateCheckService(context: Context) = coroutineScope {

    Log.d("UpdateCheckService", "Start service")

    val scope = CoroutineScope(Dispatchers.Default)

    var job: Job? = null

    Apps.entries.forEach { app ->

        val repository = app.repository

        // Update info inside repository
        if (repository.availableVersion.value != null) repository.updateInfo()

        // Available of app
        var availableVersion = ""

        // Available version
         job = scope.launch {
            repository.availableVersion.collect { version ->
                Log.d("UpdateCheckService", "availableVersion: $version")
                if (version?.isNotEmpty() == true) {
                    availableVersion = version
                    job?.cancel()
                }
            }
        }

        // Current nonRoot version
        job = scope.launch {
            Log.d("UpdateCheckService", "Start collect nonRoot versions")

            // Cancel collect flow after delay
            val delayedJob = executeAfterDelay(DELAY) {
                job?.cancel()
            }

            //Collect current version of app
            repository.nonRootVersion.collect { version ->
                if (version?.isNotEmpty() == true && version != availableVersion) {

                    // Create message for notification
                    val message = "${getStringRes(R.string.notification_message_nonRoot)} " +
                        "${repository.appName}, ${getStringRes(ru.blays.revanced.Presentation.R.string.Version)}: $availableVersion"

                    // Create notification builder with message
                    val builder = createBuilder(context, message)

                    // Show notification
                    showNotification(context, builder)

                    // Cancel launched coroutines
                    delayedJob.cancel()
                    job?.cancel()
                }
            }
        }


        // Current Root version
        job = scope.launch {
            Log.d("UpdateCheckService", "Start collect Root versions")

            // Cancel collect flow after delay
            val cancelAfterDelay = executeAfterDelay(DELAY) {
                job?.cancel()
            }

            repository.rootVersion.collect { version ->
                if (version?.isNotEmpty() == true && version != availableVersion) {

                    // Create message for notification
                    val message = "${getStringRes(R.string.notification_message_root)} " +
                        "${repository.appName}, ${getStringRes(ru.blays.revanced.Presentation.R.string.Version)}: $availableVersion"

                    // Create notification builder with message
                    val builder = createBuilder(context, message)

                    // Show notification
                    showNotification(context, builder)

                    // Cancel launched coroutines
                    cancelAfterDelay.cancel()
                    job?.cancel()
                }
            }
        }
    }
}

