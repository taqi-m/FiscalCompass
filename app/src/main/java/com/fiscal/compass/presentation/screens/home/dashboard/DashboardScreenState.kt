package com.fiscal.compass.presentation.screens.home.dashboard

import androidx.navigation.NavHostController


data class UserInfo(
    val name: String = "John Doe",
    val balance: Double = 0.0,
    val profilePictureUrl: String? = null,
)

data class DashboardScreenState(
    val appNavController: NavHostController? = null,
    val userInfo: UserInfo = UserInfo(),
    val isLoading: Boolean = false,
    val error: String? = null,
)