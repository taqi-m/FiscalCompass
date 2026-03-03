package com.fiscal.compass.domain.repository

import com.fiscal.compass.domain.model.base.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {


    suspend fun insertCategory(category: Category): Long

    suspend fun updateCategory(category: Category): Int

    suspend fun deleteCategory(category: Category): Int

    suspend fun getAllCategories(): List<Category>


    suspend fun getIncomeCategoriesWithFlow(): Flow<List<Category>>

    suspend fun getExpenseCategoriesWithFlow(): Flow<List<Category>>

    suspend fun getIncomeCategories(): List<Category>

    suspend fun getExpenseCategories(): List<Category>

    suspend fun getCategoryById(id: String): Category?
    suspend fun getCategoryNameById(id: String): String?

    suspend fun isCategoryUsedInExpenses(categoryId: String): Boolean

    suspend fun isCategoryUsedInIncomes(categoryId: String): Boolean
    suspend fun getCategoryByName(name: String): Category?

    suspend fun getNextCategoryId(): String
}