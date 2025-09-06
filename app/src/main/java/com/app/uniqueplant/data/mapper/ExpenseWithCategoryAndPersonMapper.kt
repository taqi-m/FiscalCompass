package com.app.uniqueplant.data.mapper

import com.app.uniqueplant.data.model.ExpenseWithCategoryAndPersonDbo
import com.app.uniqueplant.domain.model.ExpenseWithCategoryAndPerson

fun ExpenseWithCategoryAndPersonDbo.toDomain(): ExpenseWithCategoryAndPerson = ExpenseWithCategoryAndPerson(
    expense = expense.toDomain(),
    category = category?.toDomain(),
    person = person?.toDomain()
)