package com.app.uniqueplant.data.mappers

import com.app.uniqueplant.data.local.model.ExpenseWithCategoryDbo
import com.app.uniqueplant.domain.model.ExpenseWithCategory

fun ExpenseWithCategoryDbo.toDomain() = ExpenseWithCategory(
    expense = this.expense.toDomain(),
    category = this.category?.toDomain()
)