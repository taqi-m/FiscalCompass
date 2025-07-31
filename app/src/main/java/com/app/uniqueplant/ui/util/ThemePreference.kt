package com.app.uniqueplant.ui.util

import android.content.Context
import androidx.compose.runtime.*
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class ThemeMode(val value: Int) {
    FOLLOW_SYSTEM(0),
    LIGHT(1),
    DARK(2);

    companion object {
        fun fromValue(value: Int): ThemeMode = values().firstOrNull { it.value == value } ?: FOLLOW_SYSTEM
    }
}

private val Context.dataStore by preferencesDataStore(name = "settings")
private val THEME_MODE_KEY = intPreferencesKey("theme_mode")

class ThemePreferenceRepository(private val context: Context) {
    val themeModeFlow: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        ThemeMode.fromValue(prefs[THEME_MODE_KEY] ?: ThemeMode.FOLLOW_SYSTEM.value)
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[THEME_MODE_KEY] = mode.value }
    }


}