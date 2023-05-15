package ru.Blays.ReVanced.Manager

import android.app.Application
import com.topjohnwu.superuser.Shell
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.Blays.ReVanced.Manager.di.appModule

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(20)
        )

        /*val isRootExist = Shell.cmd("su").exec().isSuccess
        val isMagiskExist = Shell.cmd("magisk -v").exec().isSuccess

        Log.d("MagiskLog", "Magisk: $isMagiskExist, Root: $isRootExist")*/

        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }
}