package ru.Blays.ReVanced.Manager

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import org.koin.android.ext.android.inject
import ru.Blays.ReVanced.Manager.UI.Navigation.Navigator
import ru.Blays.ReVanced.Manager.UI.Theme.ReVancedManagerTheme
import ru.Blays.ReVanced.Manager.Utils.buildedTheme
import ru.Blays.ReVanced.Manager.Utils.clearInstallerCache
import ru.blays.preference.DataStores.AmoledThemeDS
import ru.blays.preference.DataStores.MonetColorsDS
import ru.blays.preference.DataStores.ThemeDS
import ru.blays.revanced.Elements.Elements.CustomRippleTheme.CustomRippleTheme
import ru.blays.revanced.shared.LogManager.BLog

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        BLog.i(TAG, "Create MainActivity")

        // Request runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            BLog.i(TAG, "Request runtime permission for api 33 and upper")
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.MANAGE_EXTERNAL_STORAGE), 1234)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            BLog.i(TAG, "Request runtime permission for api 30 and upper")
            requestPermissions(arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE), 1234)
        } else {
            BLog.i(TAG, "Request runtime permission for for api less than 30")
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1234)
        }

        BLog.d(TAG, "Clear installer cache")
        clearInstallerCache(this)

        BLog.i(TAG, "Init Compose UI")

        val themeIndexState: ThemeDS by inject()
        val monetColorsEnabled: MonetColorsDS by inject()
        val amoledThemeEnabled: AmoledThemeDS by inject()

        setContent {

            ReVancedManagerTheme(
                darkTheme = when(themeIndexState.asState().value) {
                    1 -> true
                    2 -> false
                    else -> isSystemInDarkTheme()
                },
                dynamicColor = monetColorsEnabled.asState().value,
                buildedTheme = buildedTheme(),
                isAmoledTheme = amoledThemeEnabled.asState().value
            ) {
                CompositionLocalProvider(LocalRippleTheme provides
                    CustomRippleTheme(
                        MaterialTheme.colorScheme.primary
                    )
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Navigator()
                    }
                }
            }
        }
    }
}