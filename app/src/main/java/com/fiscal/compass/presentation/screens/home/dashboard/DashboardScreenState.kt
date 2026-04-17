package com.fiscal.compass.presentation.screens.home.dashboard

import com.fiscal.compass.domain.model.Transaction
import java.util.Date

data class OverviewData(
    val name: String = "John Doe",
    val currentBalance: Double = 0.0,
    val income: Double = 0.0,
    val expenses: Double = 0.0,
    val month: Date? = null,
    val profilePictureUrl: String? = null,
)

data class DashboardScreenState(
    val overviewData: OverviewData = OverviewData(),
    val recentTransactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)