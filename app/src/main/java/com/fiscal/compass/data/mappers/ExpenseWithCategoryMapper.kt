package com.fiscal.compass.data.mappers

import com.fiscal.compass.data.local.model.ExpenseWithCategoryDbo
import com.fiscal.compass.domain.model.ExpenseWithCategory

fun ExpenseWithCategoryDbo.toDomain() = ExpenseWithCategory(
    expense = this.expense.toDomain(),
    category = this.category?.toDomain()
)