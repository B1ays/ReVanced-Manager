package ru.blays.revanced.Services.NonRootService.Util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.os.Build
import ru.blays.revanced.Services.NonRootService.InstallService.AppInstallService
import ru.blays.revanced.Services.NonRootService.InstallService.AppUninstallService
import java.io.File

private const val byteArraySize = 1024 * 1024 // Because 1,048,576 is not readable

internal object PM {

    fun installApp(apk: File, context: Context) {
        val packageInstaller = context.packageManager.packageInstaller
        val session =
            packageInstaller.openSession(packageInstaller.createSession(sessionParams))
        session.writeApk(apk)
        session.commit(context.installIntentSender)
        session.close()
    }

    fun installSplitApp(apks: Array<File>, context: Context) {
        val packageInstaller = context.packageManager.packageInstaller
        val session =
            packageInstaller.openSession(packageInstaller.createSession(sessionParams))
        for (apk in apks) {
            session.writeApk(apk)
        }
        session.commit(context.installIntentSender)
        session.close()
    }

    fun uninstallPackage(pkg: String, context: Context) {
        val packageInstaller = context.packageManager.packageInstaller
        packageInstaller.uninstall(pkg, context.uninstallIntentSender)
    }

    fun launchApp(pkg: String, context: Context) {
        val intent = context.packageManager.getLaunchIntentForPackage(pkg)
        val apk = context.packageManager.getPackageInfo(pkg, 0)
        if (intent != null) {
            context.startActivity(intent)
        }
    }

    @Suppress("DEPRECATION")
    fun getAppName(pkg: String, context: Context): String {

        val packageManager = context.packageManager

        return packageManager.getApplicationInfo(pkg, 0).name
    }
}

private fun PackageInstaller.Session.writeApk(apk: File) {
    apk.inputStream().use { inputStream ->
        openWrite(apk.name, 0, apk.length()).use { outputStream ->
            inputStream.copyTo(outputStream, byteArraySize)
            fsync(outputStream)
        }
    }
}

private val intentFlags
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        PendingIntent.FLAG_MUTABLE
    else
        0

private val sessionParams
    get() = PackageInstaller.SessionParams(
        PackageInstaller.SessionParams.MODE_FULL_INSTALL
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setInstallReason(PackageManager.INSTALL_REASON_USER)
        }
    }

private val Context.installIntentSender
    get() = PendingIntent.getService(
        this,
        0,
        Intent(this, AppInstallService::class.java),
        intentFlags
    ).intentSender

private val Context.uninstallIntentSender
    get() = PendingIntent.getService(
        this,
        0,
        Intent(this, AppUninstallService::class.java),
        intentFlags
    ).intentSender