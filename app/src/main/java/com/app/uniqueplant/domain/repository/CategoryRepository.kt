package com.app.uniqueplant.domain.repository

import com.app.uniqueplant.data.model.Category

interface CategoryRepository {

    suspend fun insertCategory(category: Category): Long

    suspend fun updateCategory(category: Category)

    suspend fun deleteCategory(category: Category)

    suspend fun getAllCategories(): List<Category>

    suspend fun getIncomeCategories(): List<Category>

    suspend fun getExpenseCategories(): List<Category>

    suspend fun getCategoryById(id: Long): Category?

    suspend fun isCategoryUsedInExpenses(categoryId: Long): Boolean

    suspend fun isCategoryUsedInIncomes(categoryId: Long): Boolean
}