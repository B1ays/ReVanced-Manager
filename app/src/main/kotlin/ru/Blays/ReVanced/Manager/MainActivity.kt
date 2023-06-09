package ru.Blays.ReVanced.Manager

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.android.ext.android.inject
import ru.Blays.ReVanced.Manager.Repository.SettingsRepository
import ru.Blays.ReVanced.Manager.UI.Navigation.Navigator
import ru.Blays.ReVanced.Manager.UI.Theme.ReVancedManagerTheme


class MainActivity : ComponentActivity() {

    private val settingsRepository: SettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.MANAGE_EXTERNAL_STORAGE), 1234)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requestPermissions(arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE), 1234)
        } else {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1234)
        }

        setContent {

            settingsRepository.isSystemInDarkMode = isSystemInDarkTheme()

            val buildedTheme by settingsRepository.buildedTheme

            val isAmoledTheme = settingsRepository.isAmoledTheme

            ReVancedManagerTheme(
                darkTheme = settingsRepository.appTheme.isDarkMode!!,
                dynamicColor = settingsRepository.monetTheme,
                buildedTheme = buildedTheme,
                isAmoledTheme = isAmoledTheme
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