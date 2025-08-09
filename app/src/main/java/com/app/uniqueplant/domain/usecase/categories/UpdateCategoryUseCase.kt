package com.app.uniqueplant.domain.usecase.categories

import com.app.uniqueplant.data.model.Category
import com.app.uniqueplant.domain.repository.CategoryRepository
import com.app.uniqueplant.presentation.admin.categories.UiState

class UpdateCategoryUseCase (
    private val categoryRepository: CategoryRepository
){
    suspend fun invoke(category: Category): UiState {

        val categoryExists = categoryRepository.getCategoryByName(category.name)
        if (categoryExists != null && categoryExists.categoryId != category.categoryId) {
            return UiState.Error("Category with name ${category.name} already exists.")
        }

        val result =  categoryRepository.updateCategory(category)
        return if (result >= 0) {
            UiState.Success("Category updated successfully")
        } else {
            UiState.Error("Unknown error")
        }
    }
}