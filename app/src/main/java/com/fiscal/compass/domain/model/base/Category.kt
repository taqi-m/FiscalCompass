package com.fiscal.compass.domain.model.base

import java.util.Date

data class Category(
    val categoryId: Long = 0,
    val parentCategoryId : Long? = null,
    val name: String,
    val color: Int = 0xFF000000.toInt(),
    val isExpenseCategory: Boolean,
    val icon: String? = null,
    val description: String? = null,
    val expectedPersonType: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

typealias CategoryTree = Map<Category, List<Category>>