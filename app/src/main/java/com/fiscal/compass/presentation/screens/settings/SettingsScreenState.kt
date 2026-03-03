package com.fiscal.compass.presentation.screens.settings

import com.fiscal.compass.domain.model.update.UpdateStatus


data class UserInfo(
    val userName: String,
    val userEmail: String,
    val profilePictureUrl: String? = null
)


data class SettingsScreenState(
    val userInfo: UserInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLogOutSuccess: Boolean = false,
    val updateStatus: UpdateStatus = UpdateStatus.NotChecked,
    val downloadProgress: Float = 0f,
    val apkDownloaded: Boolean = false
)