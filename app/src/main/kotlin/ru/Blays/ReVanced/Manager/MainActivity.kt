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
import ru.Blays.ReVanced.Manager.ui.Navigation.Navigator
import ru.blays.revanced.Presentation.Repository.SettingsRepository
import ru.blays.revanced.Presentation.Theme.ReVancedManagerTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val settingsRepository: SettingsRepository by inject()

        setContent {

            settingsRepository.isSystemInDarkMode = isSystemInDarkTheme()

            ReVancedManagerTheme(
                darkTheme = settingsRepository.appTheme.isDarkMode!!,
                dynamicColor = settingsRepository.monetTheme
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