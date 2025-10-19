package com.app.uniqueplant.domain.model

import com.app.uniqueplant.domain.model.base.Category
import com.app.uniqueplant.domain.model.base.Expense
import com.app.uniqueplant.domain.model.base.Person

data class ExpenseFull(
    val expense: Expense,
    val category: Category?,
    val person: Person?
)
