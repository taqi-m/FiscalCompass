package com.app.uniqueplant.presentation.mappers

import com.app.uniqueplant.domain.model.Category
import com.app.uniqueplant.domain.model.CategoryTree
import com.app.uniqueplant.presentation.model.CategoryUi
import com.app.uniqueplant.presentation.model.GroupedCategoryUi


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