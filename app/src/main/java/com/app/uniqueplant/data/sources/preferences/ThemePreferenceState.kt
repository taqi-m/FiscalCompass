package com.app.uniqueplant.data.sources.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey

class ThemePreferenceState(
    dataStore: DataStore<Preferences>
) : PreferenceState<Boolean>(
    key = booleanPreferencesKey("dark_mode"),
    defaultValue = false,
    dataStore = dataStore
)