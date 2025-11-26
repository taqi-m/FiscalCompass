package com.fiscal.compass.domain.model

import com.fiscal.compass.domain.model.base.Category
import com.fiscal.compass.domain.model.base.Income

data class IncomeWithCategory(
    val income: Income,
    val category: Category?
)