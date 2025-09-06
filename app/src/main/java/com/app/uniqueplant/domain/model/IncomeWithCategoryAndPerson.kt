package com.app.uniqueplant.domain.model

data class IncomeWithCategoryAndPerson(
    val income: Income,
    val category: Category?,
    val person: Person?
)
