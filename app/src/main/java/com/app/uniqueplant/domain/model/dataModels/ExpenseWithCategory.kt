package com.app.uniqueplant.domain.model.dataModels

data class ExpenseWithCategory(
    val expense: Expense,
    val category: Category?
)