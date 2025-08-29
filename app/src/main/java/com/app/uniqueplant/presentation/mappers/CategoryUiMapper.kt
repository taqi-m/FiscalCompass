package com.app.uniqueplant.presentation.mappers

import com.app.uniqueplant.domain.model.Category
import com.app.uniqueplant.domain.model.CategoryTree
import com.app.uniqueplant.presentation.model.CategoryUi
import com.app.uniqueplant.presentation.model.GroupedCategoryUi


fun Category.toCategoryUi(): CategoryUi {
    return CategoryUi(
        categoryId = this.categoryId,
        parentId = this.parentCategoryId,
        name = this.name,
        description = this.description,
        icon = this.icon,
        color = this.color.toString(16).padStart(8, '0')
    )
}

fun CategoryTree.toGroupedCategoryUi(): GroupedCategoryUi {
    return this.mapKeys {
        it.key.toCategoryUi()
    }.mapValues { entry ->
        entry.value.map { it.toCategoryUi() }
    }
}