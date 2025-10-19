package com.app.uniqueplant.domain.model

import com.app.uniqueplant.domain.model.base.Category
import com.app.uniqueplant.domain.model.base.Income

data class IncomeWithCategory(
    val income: Income,
    val category: Category?
)