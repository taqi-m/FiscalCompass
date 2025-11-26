package com.fiscal.compass.domain.model

import com.fiscal.compass.domain.model.base.Category
import com.fiscal.compass.domain.model.base.Expense

data class ExpenseWithCategory(
    val expense: Expense,
    val category: Category?
)