package com.fiscal.compass.ui.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.fiscal.compass.R
import com.fiscal.compass.di.FiscalCompassApp.Companion.applicationScope
import com.fiscal.compass.ui.theme.DEFAULT_SEED_COLOR
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Keys are now managed by DataStoreManager
// const val DARK_THEME_VALUE = "dark_theme_value"
// private const val HIGH_CONTRAST = "high_contrast"
// private const val DYNAMIC_COLOR = "dynamic_color"
// private const val THEME_COLOR = "theme_color"
// const val PALETTE_STYLE = "palette_style"

// The DataStore instance is now in DataStoreManager.kt
// private val Context.dataStore by preferencesDataStore(name = "settings")

object PreferenceUtil {

    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var coroutineScope: CoroutineScope

    // Call this from your Application class after Hilt injection
    fun initialize(manager: DataStoreManager, scope: CoroutineScope = applicationScope) {
        dataStoreManager = manager
        coroutineScope = scope
        // Initialize the state flow by collecting the first value from DataStore
        // and then continue collecting for updates.
        scope.launch {
            dataStoreManager.appSettingsFlow.collect {
                mutableAppSettingsStateFlow.value = it
            }
        }
    }

    data class AppSettings(
        val darkTheme: DarkThemePreference = DarkThemePreference(),
        val isDynamicColorEnabled: Boolean = DynamicColors.isDynamicColorAvailable(), // Default to system availability
        val seedColor: Int = DEFAULT_SEED_COLOR,
        val paletteStyleIndex: Int = 0,
    )

    // Initialize with a default value, it will be updated by DataStore
    private val mutableAppSettingsStateFlow = MutableStateFlow(AppSettings())
    val appSettingsStateFlow = mutableAppSettingsStateFlow.asStateFlow()

    fun modifyDarkThemePreference(
        darkThemeValue: Int = appSettingsStateFlow.value.darkTheme.darkThemeValue,
        isHighContrastModeEnabled: Boolean =
            appSettingsStateFlow.value.darkTheme.isHighContrastModeEnabled,
    ) {
        coroutineScope.launch(Dispatchers.IO) {
            // Update DataStore first
            dataStoreManager.saveDarkThemePreference(darkThemeValue, isHighContrastModeEnabled)
            // The flow collection in initialize() will update mutableAppSettingsStateFlow
        }
    }

    fun switchDynamicColor(
        enabled: Boolean = !mutableAppSettingsStateFlow.value.isDynamicColorEnabled
    ) {
        coroutineScope.launch(Dispatchers.IO) {
            dataStoreManager.saveDynamicColorPreference(enabled)
            // The flow collection in initialize() will update mutableAppSettingsStateFlow
        }
    }

    fun modifySeedColor(
        seedColor: Int = appSettingsStateFlow.value.seedColor
    ) {
        coroutineScope.launch(Dispatchers.IO) {
            dataStoreManager.saveSeedColorPreference(seedColor)
        }
    }

    fun modifyPaletteStyle(
        paletteStyleIndex: Int = appSettingsStateFlow.value.paletteStyleIndex
    ) {
        coroutineScope.launch(Dispatchers.IO) {
            dataStoreManager.savePaletteStylePreference(paletteStyleIndex)
        }
    }

    data class DarkThemePreference(
        val darkThemeValue: Int = FOLLOW_SYSTEM,
        val isHighContrastModeEnabled: Boolean = false,
    ) {
        companion object {
            const val FOLLOW_SYSTEM = 1
            const val ON = 2
            const val OFF = 3
        }

        @Composable
        fun isDarkTheme(): Boolean {
            return if (darkThemeValue == FOLLOW_SYSTEM) isSystemInDarkTheme() else darkThemeValue == ON
        }

        @Composable
        fun getDarkThemeDesc(): String {
            return when (darkThemeValue) {
                FOLLOW_SYSTEM -> stringResource(R.string.follow_system)
                ON -> stringResource(R.string.on)
                else -> stringResource(R.string.off)
            }
        }
    }
}
