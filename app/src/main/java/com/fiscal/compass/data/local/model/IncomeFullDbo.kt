package com.fiscal.compass.data.local.model

import androidx.room.Embedded
import androidx.room.Relation

data class IncomeFullDbo(
    @Embedded val income: IncomeEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    val category: CategoryEntity?,
    @Relation(
        parentColumn = "personId",
        entityColumn = "personId"
    )
    val person: PersonEntity?
)
