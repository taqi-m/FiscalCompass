package com.app.uniqueplant.domain.usecase.categories

import com.app.uniqueplant.data.model.Category
import com.app.uniqueplant.domain.repository.CategoryRepository
import com.app.uniqueplant.presentation.admin.categories.UiState

class DeleteCategoryUseCase(
    private val categoryRepository: CategoryRepository
) {
    suspend fun invoke(category: Category): UiState {
        // Proceed to delete the category
        val result = categoryRepository.deleteCategory(category)
        return if (result > 0) {
            UiState.Success("Category deleted successfully")
        } else {
            UiState.Error("Unknown error occurred while deleting the category")
        }
    }
}