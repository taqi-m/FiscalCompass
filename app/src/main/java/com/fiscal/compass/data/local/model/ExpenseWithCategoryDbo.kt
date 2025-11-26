package com.fiscal.compass.data.local.model

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation
@Keep
data class ExpenseWithCategoryDbo(
    @Embedded val expense: ExpenseEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    val category: CategoryEntity?
)