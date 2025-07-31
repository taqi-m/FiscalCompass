package com.app.uniqueplant.ui.util

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ThemePreferenceViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = ThemePreferenceRepository(app)
    val themeMode: StateFlow<ThemeMode> = repo.themeModeFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        ThemeMode.FOLLOW_SYSTEM
    )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { repo.setThemeMode(mode) }
    }
}