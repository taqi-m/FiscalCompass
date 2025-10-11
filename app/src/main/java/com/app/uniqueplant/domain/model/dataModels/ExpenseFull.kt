package com.app.uniqueplant.domain.model.dataModels

data class ExpenseFull(
    val expense: Expense,
    val category: Category?,
    val person: Person?
)
