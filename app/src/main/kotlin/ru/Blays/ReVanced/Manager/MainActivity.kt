package ru.Blays.ReVanced.Manager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import ru.Blays.ReVanced.Manager.di.DependencyContainer
import ru.Blays.ReVanced.Manager.ui.Navigation.Navigator
import ru.Blays.ReVanced.Manager.ui.theme.ReVancedManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dependencyContainer = DependencyContainer(this)

        setContent {
            ReVancedManagerTheme(darkTheme = true) {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                   Navigator(dependencyContainer)
                }
            }
        }
    }
}