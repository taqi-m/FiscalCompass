package com.app.uniqueplant.presentation.model

data class IncomeUi(
    val incomeId: Long = 0,
    val formatedAmount: String = "",
    val formatedDate: String = "",
    val formatedTime: String,
    val description: String? = null
)
