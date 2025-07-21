package com.app.uniqueplant.domain.usecase

import com.app.uniqueplant.data.datasource.preferences.SharedPreferencesRepository

class SwitchThemeUseCase(
    private val preference: SharedPreferencesRepository
) {

    fun execute(isDarkMode: Boolean) {
        preference.setDarkMode(isDarkMode)
    }
}