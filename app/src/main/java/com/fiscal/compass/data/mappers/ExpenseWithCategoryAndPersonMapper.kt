package com.fiscal.compass.data.mappers

import com.fiscal.compass.data.local.model.ExpenseFullDbo
import com.fiscal.compass.domain.model.ExpenseFull

fun ExpenseFullDbo.toDomain(): ExpenseFull = ExpenseFull(
    expense = expense.toDomain(),
    category = category?.toDomain(),
    person = person?.toDomain()
)