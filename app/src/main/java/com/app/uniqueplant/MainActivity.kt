package com.app.uniqueplant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.app.uniqueplant.domain.repository.AppPreferenceRepository
import com.app.uniqueplant.presentation.navigation.AppNavigation
import com.app.uniqueplant.ui.theme.UniquePlantTheme
import com.app.uniqueplant.ui.util.LocalDarkTheme
import com.app.uniqueplant.ui.util.SettingsProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var preferenceManager: AppPreferenceRepository
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            SettingsProvider(
            ) {
                UniquePlantTheme (
                    darkTheme = LocalDarkTheme.current.isDarkTheme()
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()
                        AppNavigation(navController = navController, preferenceManager)
                    }
                }
            }
        }
    }
}
