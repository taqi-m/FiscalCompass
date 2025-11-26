package com.fiscal.compass.domain.model

import com.fiscal.compass.domain.model.base.Category
import com.fiscal.compass.domain.model.base.Income
import com.fiscal.compass.domain.model.base.Person

data class IncomeFull(
    val income: Income,
    val category: Category?,
    val person: Person?
)
