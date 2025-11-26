package com.fiscal.compass.data.mappers

import com.fiscal.compass.data.local.model.IncomeFullDbo
import com.fiscal.compass.domain.model.IncomeFull

fun IncomeFullDbo.toDomain(): IncomeFull = IncomeFull(
    income = income.toDomain(),
    category = category?.toDomain(),
    person = person?.toDomain()
)