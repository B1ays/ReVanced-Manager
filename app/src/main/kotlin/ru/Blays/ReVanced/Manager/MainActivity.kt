package ru.Blays.ReVanced.Manager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import org.koin.android.ext.android.inject
import ru.Blays.ReVanced.Manager.Repository.SettingsRepository
import ru.Blays.ReVanced.Manager.UI.Navigation.Navigator
import ru.Blays.ReVanced.Manager.UI.Theme.ReVancedManagerTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsRepository: SettingsRepository by inject()

        val buildedTheme = settingsRepository.buildedTheme

        setContent {

            settingsRepository.isSystemInDarkMode = isSystemInDarkTheme()

            ReVancedManagerTheme(
                darkTheme = settingsRepository.appTheme.isDarkMode!!,
                dynamicColor = settingsRepository.monetTheme,
                buildedTheme = buildedTheme
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