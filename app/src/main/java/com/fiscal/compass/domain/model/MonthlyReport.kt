package com.fiscal.compass.domain.model

data class MonthlyReport(
    val incomes: Map<String, Double>,
    val expenses: Map<String, Double>,
    val totalIncomes: Double,
    val totalExpenses: Double,
    val totalProfit: Double
)
