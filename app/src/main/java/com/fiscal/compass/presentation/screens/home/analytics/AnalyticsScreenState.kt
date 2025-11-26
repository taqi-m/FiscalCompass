package com.fiscal.compass.presentation.screens.home.analytics

data class AnalyticsScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val expenses: Map<String, Double> = emptyMap(),
    val incomes: Map<String, Double> = emptyMap(),
    val totalIncomes: Double = 0.0,
    val totalExpenses : Double = 0.0,
    val totalProfit : Double = 0.0,
)