package com.fiscal.compass.domain.usecase.categories

import com.fiscal.compass.domain.model.base.Category
import com.fiscal.compass.domain.repository.CategoryRepository
import com.fiscal.compass.presentation.screens.category.UiState
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend fun invoke(category: Category): UiState {
        // Proceed to delete the categoryId
        val result = categoryRepository.deleteCategory(category)
        return if (result > 0) {
            UiState.Success("Category deleted successfully")
        } else {
            UiState.Error("Unknown error occurred while deleting the categoryId")
        }
    }
}