package com.app.uniqueplant.domain.model

import com.app.uniqueplant.domain.model.base.Category
import com.app.uniqueplant.domain.model.base.Expense

data class ExpenseWithCategory(
    val expense: Expense,
    val category: Category?
)