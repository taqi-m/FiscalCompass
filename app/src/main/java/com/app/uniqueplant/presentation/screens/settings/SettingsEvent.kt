package com.app.uniqueplant.presentation.screens.settings

sealed class SettingsEvent {
    data class SampleEventWithParameter(val value: String) : SettingsEvent()
    data class OnThemeSwitchChanged(val checked: Boolean) : SettingsEvent()

    object OnLogoutClicked : SettingsEvent()

}