package ru.Blays.ReVanced.Manager.BackgroundService

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Environment
import ru.Blays.ReVanced.Manager.CrashHandlerActivity
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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

        if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val formattedDate = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(Date())
            val fileName = "crash_$formattedDate.txt"
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
            file.writeText(stackTrace.toString())
        }

        context.startActivity(intent)

        exitProcess(1)
    }
}
