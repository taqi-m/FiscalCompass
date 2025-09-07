package com.app.uniqueplant.data.mapper

import com.app.uniqueplant.data.model.ExpenseFullDbo
import com.app.uniqueplant.domain.model.ExpenseFull

fun ExpenseFullDbo.toDomain(): ExpenseFull = ExpenseFull(
    expense = expense.toDomain(),
    category = category?.toDomain(),
    person = person?.toDomain()
)