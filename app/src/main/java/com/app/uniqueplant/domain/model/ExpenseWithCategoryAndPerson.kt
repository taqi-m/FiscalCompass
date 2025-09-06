package com.app.uniqueplant.domain.model

data class ExpenseWithCategoryAndPerson(
    val expense: Expense,
    val category: Category?,
    val person: Person?
)
