package com.app.uniqueplant.presentation.settings


data class UserInfo(
    val userName: String,
    val userEmail: String,
    val profilePictureUrl: String? = null
)


data class SettingsScreenState(
    val userInfo: UserInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLogOutSuccess: Boolean = false
    // Add your state properties here
)