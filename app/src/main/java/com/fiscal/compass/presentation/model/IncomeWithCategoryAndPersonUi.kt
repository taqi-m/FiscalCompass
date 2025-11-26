package com.fiscal.compass.presentation.model

data class IncomeWithCategoryAndPersonUi(
    val income: IncomeUi,
    val category: CategoryUi?,
    val person: PersonUi?
)
