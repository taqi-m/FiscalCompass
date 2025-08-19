package com.app.uniqueplant.domain.usecase.categories

import com.app.uniqueplant.domain.model.Category
import com.app.uniqueplant.presentation.model.TransactionType
import com.app.uniqueplant.domain.repository.CategoryRepository
import com.app.uniqueplant.presentation.admin.categories.UiState

class AddCategoryUseCase(
    private val categoryRepository: CategoryRepository
) {
    suspend fun invoke(name: String, description: String, transactionType: TransactionType, expectedPersonType: String): UiState {
        // Check if the category already exists
        val existingCategory = categoryRepository.getCategoryByName(name)
        if (existingCategory != null) {
            return UiState.Error("Category with name $name already exists.")
        }

        val isExpenseCategory = transactionType == TransactionType.EXPENSE

        // Create a new category
        val newCategory = Category(
            name = name,
            description = description,
            expectedPersonType = expectedPersonType,
            isExpenseCategory = isExpenseCategory,
            color = 0xFFFFFF.toInt(),
        )

        // Add the new category to the repository
        val result = categoryRepository.insertCategory(newCategory)
        return if (result >= 0) {
            UiState.Success("Category added successfully")
        } else {
            UiState.Error("Unknown error occurred while adding category")
        }
    }
}