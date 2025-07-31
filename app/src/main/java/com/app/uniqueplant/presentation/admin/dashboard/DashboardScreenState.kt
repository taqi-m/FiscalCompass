package com.app.uniqueplant.presentation.admin.dashboard


data class UserInfo(
    val name: String = "John Doe",
    val balance: Double = 45028.0,
    val profilePictureUrl: String? = null,
)

data class DashboardScreenState(
    val userInfo: UserInfo = UserInfo(),
    val isLoading: Boolean = false,
    val error: String? = null,
)