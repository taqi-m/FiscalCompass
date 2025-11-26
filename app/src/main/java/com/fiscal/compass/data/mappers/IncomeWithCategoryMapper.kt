package com.fiscal.compass.data.mappers

import com.fiscal.compass.data.local.model.IncomeWithCategoryDbo
import com.fiscal.compass.domain.model.IncomeWithCategory

fun IncomeWithCategoryDbo.toIncomeWithCategory() = IncomeWithCategory(
    income = this.income.toDomain(),
    category = this.category?.toDomain()
)