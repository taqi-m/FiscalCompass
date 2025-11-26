package com.fiscal.compass.ui.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fiscal.compass.ui.theme.DEFAULT_SEED_COLOR
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Define a name for the DataStore
private const val SETTINGS_PREFERENCES_NAME = "settings_preferences"

// Create the DataStore instance using the preferencesDataStore delegate
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS_PREFERENCES_NAME)

@Singleton
class DataStoreManager @Inject constructor(@ApplicationContext private val context: Context) {

    // Define Preference Keys
    private object PreferenceKeys {
        val DARK_THEME_VALUE = intPreferencesKey("dark_theme_value")
        val HIGH_CONTRAST = booleanPreferencesKey("high_contrast")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val THEME_COLOR = intPreferencesKey("theme_color")
        val PALETTE_STYLE = intPreferencesKey("palette_style")
    }

    // Read Dark Theme Preference
    val darkThemePreferenceFlow: Flow<PreferenceUtil.DarkThemePreference> = context.dataStore.data
        .map { preferences ->
            val darkThemeValue = preferences[PreferenceKeys.DARK_THEME_VALUE] ?: PreferenceUtil.DarkThemePreference.FOLLOW_SYSTEM
            val isHighContrast = preferences[PreferenceKeys.HIGH_CONTRAST] ?: false
            PreferenceUtil.DarkThemePreference(darkThemeValue, isHighContrast)
        }
        .distinctUntilChanged()

    // Save Dark Theme Preference
    suspend fun saveDarkThemePreference(darkThemeValue: Int, isHighContrastModeEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.DARK_THEME_VALUE] = darkThemeValue
            preferences[PreferenceKeys.HIGH_CONTRAST] = isHighContrastModeEnabled
        }
    }

    // Read Dynamic Color Preference
    val dynamicColorPreferenceFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.DYNAMIC_COLOR] ?: DynamicColors.isDynamicColorAvailable()
        }
        .distinctUntilChanged()

    // Save Dynamic Color Preference
    suspend fun saveDynamicColorPreference(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.DYNAMIC_COLOR] = enabled
        }
    }

    // Read Seed Color Preference
    val seedColorPreferenceFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.THEME_COLOR] ?: DEFAULT_SEED_COLOR
        }
        .distinctUntilChanged()

    // Save Seed Color Preference
    suspend fun saveSeedColorPreference(seedColor: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.THEME_COLOR] = seedColor
        }
    }

    // Read Palette Style Preference
    val paletteStylePreferenceFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.PALETTE_STYLE] ?: 0
        }
        .distinctUntilChanged()

    // Save Palette Style Preference
    suspend fun savePaletteStylePreference(paletteStyleIndex: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.PALETTE_STYLE] = paletteStyleIndex
        }
    }

    // Combined AppSettings Flow (optional, but can be useful)
    val appSettingsFlow: Flow<PreferenceUtil.AppSettings> = context.dataStore.data
        .map { preferences ->
            val darkThemeValue = preferences[PreferenceKeys.DARK_THEME_VALUE] ?: PreferenceUtil.DarkThemePreference.FOLLOW_SYSTEM
            val isHighContrast = preferences[PreferenceKeys.HIGH_CONTRAST] ?: false
            val isDynamicColorEnabled = preferences[PreferenceKeys.DYNAMIC_COLOR] ?: DynamicColors.isDynamicColorAvailable()
            val seedColor = preferences[PreferenceKeys.THEME_COLOR] ?: DEFAULT_SEED_COLOR
            val paletteStyleIndex = preferences[PreferenceKeys.PALETTE_STYLE] ?: 0

            PreferenceUtil.AppSettings(
                darkTheme = PreferenceUtil.DarkThemePreference(darkThemeValue, isHighContrast),
                isDynamicColorEnabled = isDynamicColorEnabled,
                seedColor = seedColor,
                paletteStyleIndex = paletteStyleIndex
            )
        }
        .distinctUntilChanged()
}
