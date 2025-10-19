package com.app.uniqueplant.domain.usecase.categories

import com.app.uniqueplant.domain.model.base.Category
import com.app.uniqueplant.domain.model.base.CategoryTree
import com.app.uniqueplant.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend fun getAllCategories(): List<Category> = categoryRepository.getAllCategories()

    suspend fun getAllCategoryTreeFlow(): Flow<CategoryTree> = categoryRepository.getAllCategoriesTreeFlow()

    suspend fun getExpenseCategoryTreeFlow(): Flow<CategoryTree> = categoryRepository.getExpenseCategoriesTreeFLow()

    suspend fun getIncomeCategoryTreeFlow(): Flow<CategoryTree> = categoryRepository.getIncomeCategoriesTreeFLow()

    suspend fun getIncomeCategoriesTree(): CategoryTree = categoryRepository.getIncomeCategoriesTree()

    suspend fun getExpenseCategoriesTree(): CategoryTree = categoryRepository.getExpenseCategoriesTree()

    suspend fun getExpenseCategoriesWithFlow(): Flow<List<Category>> = categoryRepository.getExpenseCategoriesWithFlow()

    suspend fun getIncomeCategoriesWithFlow(): Flow<List<Category>> = categoryRepository.getIncomeCategoriesWithFlow()

    suspend fun getExpenseCategories(): List<Category> = categoryRepository.getExpenseCategories()

    suspend fun getIncomeCategories(): List<Category> = categoryRepository.getIncomeCategories()

    suspend fun getCategoryById(id: Long): Category? = categoryRepository.getCategoryById(id)
}
