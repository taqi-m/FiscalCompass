package com.fiscal.compass.data.local.preferences

import com.fiscal.compass.domain.interfaces.Preferences
import com.fiscal.compass.domain.repository.PreferenceManager
import javax.inject.Inject

class PreferencesImpl @Inject constructor(
    private val preferenceManager: PreferenceManager
) : Preferences {
    override fun saveInt(key: String, value: Int) {
        preferenceManager.saveInt(key, value)
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return preferenceManager.getInt(key, defaultValue)
    }
}