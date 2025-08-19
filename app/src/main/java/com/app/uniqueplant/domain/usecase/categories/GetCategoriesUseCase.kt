package com.app.uniqueplant.domain.usecase.categories

import com.app.uniqueplant.domain.model.Category
import com.app.uniqueplant.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow

class GetCategoriesUseCase(
    private val categoryRepository: CategoryRepository
) {
    suspend fun getAllCategories(): List<Category> = categoryRepository.getAllCategories()

    suspend fun getExpenseCategoriesWithFlow(): Flow<List<Category>> = categoryRepository.getExpenseCategoriesWithFlow()

    suspend fun getIncomeCategoriesWithFlow(): Flow<List<Category>> = categoryRepository.getIncomeCategoriesWithFlow()

    suspend fun getExpenseCategories(): List<Category> = categoryRepository.getExpenseCategories()

    suspend fun getIncomeCategories(): List<Category> = categoryRepository.getIncomeCategories()

    suspend fun getCategoryById(id: Long): Category? = categoryRepository.getCategoryById(id)
}
