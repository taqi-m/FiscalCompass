package com.app.uniqueplant.data.mapper

import com.app.uniqueplant.data.model.IncomeWithCategoryDbo
import com.app.uniqueplant.domain.model.IncomeWithCategory

fun IncomeWithCategoryDbo.toIncomeWithCategory() = IncomeWithCategory(
    income = this.income.toDomain(),
    category = this.category?.toDomain()
)