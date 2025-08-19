package com.app.uniqueplant.data.mapper

import com.app.uniqueplant.data.model.CategoryEntity
import com.app.uniqueplant.domain.model.Category

/**
 * Mapper functions to convert between [CategoryEntity] and [Category].
 * These functions are used to convert data between the database layer and the domain layer.
 */
fun CategoryEntity.toCategory(): Category {
    return Category(
        categoryId = this.categoryId,
        name = this.name,
        color = this.color,
        isExpenseCategory = this.isExpenseCategory,
        icon = this.icon,
        description = this.description,
        expectedPersonType = this.expectedPersonType,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun Category.toCategoryEntity(): CategoryEntity {
    return CategoryEntity(
        categoryId = this.categoryId,
        name = this.name,
        color = this.color,
        isExpenseCategory = this.isExpenseCategory,
        icon = this.icon,
        description = this.description,
        expectedPersonType = this.expectedPersonType,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}