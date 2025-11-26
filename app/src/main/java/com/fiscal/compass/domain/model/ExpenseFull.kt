package com.fiscal.compass.domain.model

import com.fiscal.compass.domain.model.base.Category
import com.fiscal.compass.domain.model.base.Expense
import com.fiscal.compass.domain.model.base.Person

data class ExpenseFull(
    val expense: Expense,
    val category: Category?,
    val person: Person?
)
