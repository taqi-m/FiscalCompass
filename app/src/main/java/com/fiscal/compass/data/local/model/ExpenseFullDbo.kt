package com.fiscal.compass.data.local.model

import androidx.room.Embedded
import androidx.room.Relation

data class ExpenseFullDbo(
    @Embedded val expense: ExpenseEntity,
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
