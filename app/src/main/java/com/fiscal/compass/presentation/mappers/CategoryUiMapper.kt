package com.fiscal.compass.presentation.mappers

import com.fiscal.compass.domain.model.base.Category
import com.fiscal.compass.presentation.model.CategoryUi


fun Category.toUi(): CategoryUi {
    return CategoryUi(
        categoryId = this.categoryId,
        isExpenseCategory = this.isExpenseCategory,
        name = this.name,
        description = this.description,
        icon = this.icon,
        color = this.color.toString(16).padStart(8, '0')
    )
}

fun CategoryUi.toCategory(): Category {
    return Category(
        categoryId = this.categoryId,
        isExpenseCategory = this.isExpenseCategory,
        name = this.name,
        description = this.description,
        icon = this.icon,
        color = this.color.toInt(16)
    )
}
