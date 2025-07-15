package com.app.uniqueplant.domain.usecase

import com.app.uniqueplant.data.model.Category
import com.app.uniqueplant.domain.repository.CategoryRepository

class GetCategoriesUseCase(
    private val categoryRepository: CategoryRepository
) {
    suspend fun getAllCategories(): List<Category> = categoryRepository.getAllCategories()

    suspend fun getExpenseCategories(): List<Category> = categoryRepository.getExpenseCategories()

    suspend fun getIncomeCategories(): List<Category> = categoryRepository.getIncomeCategories()

    suspend fun getCategoryById(id: Long): Category? = categoryRepository.getCategoryById(id)
}
