package com.app.uniqueplant.domain.usecase.categories

import com.app.uniqueplant.data.model.Category
import com.app.uniqueplant.domain.repository.CategoryRepository

class DeleteCategoryUseCase(
    private val categoryRepository: CategoryRepository
) {
    suspend fun invoke(category: Category): Result<String> {
        return categoryRepository.deleteCategory(category)
    }
}