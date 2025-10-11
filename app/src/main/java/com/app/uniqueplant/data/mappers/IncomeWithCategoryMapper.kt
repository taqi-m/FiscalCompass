package com.app.uniqueplant.data.mappers

import com.app.uniqueplant.data.local.model.IncomeWithCategoryDbo
import com.app.uniqueplant.domain.model.dataModels.IncomeWithCategory

fun IncomeWithCategoryDbo.toIncomeWithCategory() = IncomeWithCategory(
    income = this.income.toDomain(),
    category = this.category?.toDomain()
)