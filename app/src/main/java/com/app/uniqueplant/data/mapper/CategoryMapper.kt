package com.app.uniqueplant.data.mapper

import com.app.uniqueplant.data.model.CategoryEntity
import com.app.uniqueplant.domain.model.Category
import com.app.uniqueplant.domain.model.CategoryTree
import java.util.Date

/**
 * Mapper functions to convert between [CategoryEntity] and [Category].
 * These functions are used to convert data between the database layer and the domain layer.
 */
fun CategoryEntity.toDomain(): Category {
    return Category(
        categoryId = this.categoryId,
        parentCategoryId = this.parentCategoryId,
        name = this.name,
        color = this.color,
        isExpenseCategory = this.isExpenseCategory,
        icon = this.icon,
        description = this.description,
        expectedPersonType = this.expectedPersonType,
        createdAt = Date(this.createdAt),
        updatedAt = Date(this.updatedAt)
    )
}

/**
 * Converts a [Category] to a [CategoryEntity].
 *
 * @return A [CategoryEntity] representation of the [Category].
 */
fun Category.toCategoryEntity(parentId: Long? = null): CategoryEntity {
    return CategoryEntity(
        categoryId = this.categoryId,
        name = this.name,
        color = this.color,
        isExpenseCategory = this.isExpenseCategory,
        icon = this.icon,
        description = this.description,
        expectedPersonType = this.expectedPersonType,
        parentCategoryId = parentId ?: this.parentCategoryId,
        createdAt = this.createdAt.time,
        updatedAt = this.updatedAt.time,
    )
}


/**
 * Converts a list of [CategoryEntity] to a [CategoryTree].
 *
 * @return A [CategoryTree] representation of the list of [CategoryEntity].
 */
fun List<CategoryEntity>.toCategoryTree(): CategoryTree {
    val categories = this.map { it.toDomain() }
    val byParent = categories.groupBy { it.parentCategoryId }

    val result = mutableMapOf<Category, List<Category>>()

    categories.filter { it.parentCategoryId == null }.forEach { root ->
        result[root] = buildSubTree(root, byParent)
    }

    result.forEach { category, categories ->
    }

    return result
}

private fun buildSubTree(parent: Category, grouped: Map<Long?, List<Category>>): List<Category> {
    return grouped[parent.categoryId] ?: emptyList()
}


fun Map<Category, List<Category>>.toEntityList(): List<CategoryEntity> {
    val result = mutableListOf<CategoryEntity>()
    var tempId = -1L

    this.forEach { (parent, children) ->
        val parentEntity = parent.toCategoryEntity(parentId = null).copy(categoryId = tempId--)
        result.add(parentEntity)

        children.forEach { child ->
            val childEntity = child.toCategoryEntity(parentId = parentEntity.categoryId)
            result.add(childEntity)
        }
    }
    return result
}
