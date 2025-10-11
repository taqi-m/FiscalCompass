package com.app.uniqueplant.data.mappers

import com.app.uniqueplant.data.local.model.ExpenseFullDbo
import com.app.uniqueplant.domain.model.dataModels.ExpenseFull

fun ExpenseFullDbo.toDomain(): ExpenseFull = ExpenseFull(
    expense = expense.toDomain(),
    category = category?.toDomain(),
    person = person?.toDomain()
)