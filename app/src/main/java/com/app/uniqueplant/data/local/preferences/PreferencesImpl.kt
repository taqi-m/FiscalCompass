package com.app.uniqueplant.data.local.preferences

import com.app.uniqueplant.domain.interfaces.Preferences
import com.app.uniqueplant.domain.repository.PreferenceManager
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