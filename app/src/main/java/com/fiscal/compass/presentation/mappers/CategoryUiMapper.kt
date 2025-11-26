package com.fiscal.compass.presentation.mappers

import com.fiscal.compass.domain.model.base.Category
import com.fiscal.compass.domain.model.base.CategoryTree
import com.fiscal.compass.presentation.model.CategoryUi
import com.fiscal.compass.presentation.model.GroupedCategoryUi


fun Category.toUi(): CategoryUi {
    return CategoryUi(
        categoryId = this.categoryId,
        parentId = this.parentCategoryId,
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
        parentCategoryId = this.parentId,
        isExpenseCategory = this.isExpenseCategory,
        name = this.name,
        description = this.description,
        icon = this.icon,
        color = this.color.toInt(16)
    )
}

fun CategoryTree.toGroupedCategoryUi(): GroupedCategoryUi {
    return this.mapKeys {
        it.key.toUi()
    }.mapValues { entry ->
        entry.value.map { it.toUi() }
    }
}