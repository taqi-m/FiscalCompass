package com.fiscal.compass.presentation.model

data class ExpenseWithCategoryAndPersonUi(
    val expense: ExpenseUi,
    val category: CategoryUi?,
    val person: PersonUi?
)

