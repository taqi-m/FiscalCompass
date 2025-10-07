package com.app.uniqueplant.data.local.model

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation

@Keep
data class IncomeWithPersonDbo(
    @Embedded
    val income: IncomeEntity,
    @Relation(parentColumn = "personId", entityColumn = "personId")
    val person: PersonEntity?
)
