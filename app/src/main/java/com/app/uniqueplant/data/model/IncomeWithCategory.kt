package com.app.uniqueplant.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class IncomeWithCategory(
    @Embedded val income: IncomeEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    val category: CategoryEntity?
)