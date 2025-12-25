package com.fiscal.compass.domain.service

import com.fiscal.compass.domain.model.base.Category
import com.fiscal.compass.domain.model.base.CategoryTree
import com.fiscal.compass.presentation.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface CategoryService {
    /**
     * Adds a new category
     * @return Result with success message or error
     */
    suspend fun addCategory(
        name: String,
        description: String,
        transactionType: TransactionType,
        expectedPersonType: String
    ): Result<String>

    /**
     * Updates an existing category
     * @return Result with success message or error
     */
    suspend fun updateCategory(category: Category): Result<String>

    /**
     * Deletes a category
     * @return Result with success message or error
     */
    suspend fun deleteCategory(category: Category): Result<String>

    /**
     * Gets all categories
     */
    suspend fun getAllCategories(): List<Category>

    /**
     * Gets all categories as a tree structure with Flow
     */
    suspend fun getAllCategoryTreeFlow(): Flow<CategoryTree>

    /**
     * Gets expense categories as a tree structure with Flow
     */
    suspend fun getExpenseCategoryTreeFlow(): Flow<CategoryTree>

    /**
     * Gets income categories as a tree structure with Flow
     */
    suspend fun getIncomeCategoryTreeFlow(): Flow<CategoryTree>

    /**
     * Gets income categories as a tree structure
     */
    suspend fun getIncomeCategoriesTree(): CategoryTree

    /**
     * Gets expense categories as a tree structure
     */
    suspend fun getExpenseCategoriesTree(): CategoryTree

    /**
     * Gets expense categories with Flow
     */
    suspend fun getExpenseCategoriesWithFlow(): Flow<List<Category>>

    /**
     * Gets income categories with Flow
     */
    suspend fun getIncomeCategoriesWithFlow(): Flow<List<Category>>

    /**
     * Gets expense categories
     */
    suspend fun getExpenseCategories(): List<Category>

    /**
     * Gets income categories
     */
    suspend fun getIncomeCategories(): List<Category>

    /**
     * Gets a category by its ID
     */
    suspend fun getCategoryById(id: Long): Category?

    /**
     * Gets a category by its name
     */
    suspend fun getCategoryByName(name: String): Category?
}
