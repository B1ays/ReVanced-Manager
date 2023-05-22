package ru.Blays.ReVanced.Manager

import android.app.Application
import com.topjohnwu.superuser.Shell
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.Blays.ReVanced.Manager.DI.appModule
import ru.blays.revanced.Elements.DI.composeElementsModule

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(20)
        )

        startKoin {
            androidContext(this@App)
            modules(appModule)
            modules(composeElementsModule)
        }
    }
}