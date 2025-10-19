package com.app.uniqueplant.domain.model

import com.app.uniqueplant.domain.model.base.Category
import com.app.uniqueplant.domain.model.base.Income
import com.app.uniqueplant.domain.model.base.Person

data class IncomeFull(
    val income: Income,
    val category: Category?,
    val person: Person?
)
