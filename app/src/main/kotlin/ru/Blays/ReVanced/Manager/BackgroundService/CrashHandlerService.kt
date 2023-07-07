package ru.Blays.ReVanced.Manager.BackgroundService

import android.content.Context
import android.content.Intent
import ru.Blays.ReVanced.Manager.CrashHandlerActivity
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

class CrashHandlerService(private val context: Context) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(thread: Thread, exception: Throwable) {

        val stackTrace = StringWriter()

        exception.printStackTrace(PrintWriter(stackTrace))

        val intent = Intent(context, CrashHandlerActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("StackTrace", stackTrace.toString())
        }

        context.startActivity(intent)

        exitProcess(1)
    }
}
