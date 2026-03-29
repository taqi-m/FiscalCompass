package com.fiscal.compass.presentation.screens.home.dashboard

import com.fiscal.compass.domain.model.Transaction
import java.util.Date

data class UserInfo(
    val name: String = "John Doe",
    val balance: Double = 0.0,
    val month: Date? = null,
    val profilePictureUrl: String? = null,
)

data class DashboardScreenState(
    val userInfo: UserInfo = UserInfo(),
    val recentTransactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)