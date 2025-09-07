package com.app.uniqueplant.data.mapper

import com.app.uniqueplant.data.model.IncomeFullDbo
import com.app.uniqueplant.domain.model.IncomeFull

fun IncomeFullDbo.toDomain(): IncomeFull = IncomeFull(
    income = income.toDomain(),
    category = category?.toDomain(),
    person = person?.toDomain()
)