package com.app.uniqueplant.presentation.model

data class ExpenseUi(
    val expenseId: Long = 0,
    val amount: Double = 0.0,
    val formatedDate: String = "",
    val description: String? = null,
    val personName: String? = null,
    val personId: Long? = null,
    val categoryName: String? = null,
    val categoryId: Long? = null
)
