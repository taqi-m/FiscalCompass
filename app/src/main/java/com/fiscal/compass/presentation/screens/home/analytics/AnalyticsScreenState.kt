package com.fiscal.compass.presentation.screens.home.analytics

data class AnalyticsScreenState(
    val displayState: AnalyticsDisplayState? = null,
    val selectedMonth: Int? = null,
    val selectedYear: Int? = null,
)

sealed interface AnalyticsDisplayState {
    data object Loading : AnalyticsDisplayState

    data class Error(
        val message: String,
    ) : AnalyticsDisplayState

    data class Content(
        val expenses: Map<String, Double>,
        val incomes: Map<String, Double>,
        val totalIncomes: Double,
        val totalExpenses: Double,
        val totalProfit: Double,
    ) : AnalyticsDisplayState
}
