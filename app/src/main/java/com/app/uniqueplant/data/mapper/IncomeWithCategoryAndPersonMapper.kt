package com.app.uniqueplant.data.mapper

import com.app.uniqueplant.data.model.IncomeWithCategoryAndPersonDbo
import com.app.uniqueplant.domain.model.IncomeWithCategoryAndPerson

fun IncomeWithCategoryAndPersonDbo.toDomain(): IncomeWithCategoryAndPerson = IncomeWithCategoryAndPerson(
    income = income.toDomain(),
    category = category?.toDomain(),
    person = person?.toDomain()
)