package com.app.uniqueplant.domain.model

data class ExpenseWithCategory(
    val expense: Expense,
    val category: Category?
)