package com.fiscal.compass.presentation.screens.settings

sealed class SettingsEvent {
    data object OnLogoutClicked : SettingsEvent()
    data object CheckForUpdate : SettingsEvent()
    data object DownloadUpdate : SettingsEvent()
    data object InstallUpdate : SettingsEvent()
    data object DismissUpdateDialog : SettingsEvent()
}