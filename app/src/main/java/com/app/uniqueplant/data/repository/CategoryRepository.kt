package com.app.uniqueplant.data.repository

import com.app.uniqueplant.data.model.Category

interface CategoryRepository {
    suspend fun addDefaultCategories()

    suspend fun insertCategory(category: Category): Long

    suspend fun updateCategory(category: Category)

    suspend fun deleteCategory(category: Category)

    suspend fun getCategoryById(id: Long): Category?

    suspend fun isCategoryUsedInExpenses(categoryId: Long): Boolean

    suspend fun isCategoryUsedInIncomes(categoryId: Long): Boolean
}