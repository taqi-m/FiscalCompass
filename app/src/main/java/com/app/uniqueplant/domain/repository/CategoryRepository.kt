package com.app.uniqueplant.domain.repository

import com.app.uniqueplant.domain.model.dataModels.Category
import com.app.uniqueplant.domain.model.dataModels.CategoryTree
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {


    suspend fun insertCategory(category: Category): Long

    suspend fun updateCategory(category: Category): Int

    suspend fun deleteCategory(category: Category): Int

    suspend fun getAllCategories(): List<Category>

    suspend fun getAllCategoriesTreeFlow(): Flow<CategoryTree>

    suspend fun getExpenseCategoriesTreeFLow(): Flow<CategoryTree>

    suspend fun getExpenseCategoriesTree(): CategoryTree

    suspend fun getIncomeCategoriesTreeFLow(): Flow<CategoryTree>

    suspend fun getIncomeCategoriesTree(): CategoryTree

    suspend fun seedDefaultCategories(defaultCategories: Map<Category, List<Category>>)

    suspend fun getIncomeCategoriesWithFlow(): Flow<List<Category>>

    suspend fun getExpenseCategoriesWithFlow(): Flow<List<Category>>

    suspend fun getIncomeCategories(): List<Category>

    suspend fun getExpenseCategories(): List<Category>

    suspend fun getCategoryById(id: Long): Category?
    suspend fun getCategoryNameById(id: Long): String?

    suspend fun isCategoryUsedInExpenses(categoryId: Long): Boolean

    suspend fun isCategoryUsedInIncomes(categoryId: Long): Boolean
    suspend fun getCategoryByName(name: String): Category?
}