package com.app.uniqueplant.presentation.model

data class CategoryUi(
    val categoryId: Long = 0,
    val parentId: Long? = null,
    val isExpenseCategory: Boolean,
    val name: String = "",
    val description: String? = null,
    val icon: String? = null,
    val color: String

)


typealias GroupedCategoryUi = Map<CategoryUi, List<CategoryUi>>