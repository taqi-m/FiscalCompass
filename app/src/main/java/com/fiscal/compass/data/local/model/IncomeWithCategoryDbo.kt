package com.fiscal.compass.data.local.model

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation
@Keep
data class IncomeWithCategoryDbo(
    @Embedded val income: IncomeEntity,
    @Relation(parentColumn = "categoryId", entityColumn = "categoryId")
    val category: CategoryEntity?
)