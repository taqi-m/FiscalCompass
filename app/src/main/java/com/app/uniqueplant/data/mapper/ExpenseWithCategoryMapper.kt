package com.app.uniqueplant.data.mapper

import com.app.uniqueplant.data.model.ExpenseWithCategoryDbo
import com.app.uniqueplant.domain.model.ExpenseWithCategory

fun ExpenseWithCategoryDbo.toDomain() = ExpenseWithCategory(
    expense = this.expense.toDomain(),
    category = this.category?.toDomain()
)