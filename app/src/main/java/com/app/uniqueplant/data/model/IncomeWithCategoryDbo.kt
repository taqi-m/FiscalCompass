package com.app.uniqueplant.data.model

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation
@Keep
data class IncomeWithCategoryDbo(
    @Embedded val income: IncomeEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    val category: CategoryEntity?
)