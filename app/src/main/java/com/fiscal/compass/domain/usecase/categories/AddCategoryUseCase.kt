package com.fiscal.compass.domain.usecase.categories

import com.fiscal.compass.domain.model.base.Category
import com.fiscal.compass.domain.repository.CategoryRepository
import com.fiscal.compass.presentation.model.TransactionType
import com.fiscal.compass.presentation.screens.category.UiState
import javax.inject.Inject

class AddCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend fun invoke(name: String, description: String, transactionType: TransactionType, expectedPersonType: String): UiState {
        // Check if the categoryId already exists
        val existingCategory = categoryRepository.getCategoryByName(name)
        if (existingCategory != null) {
            return UiState.Error("Category with name $name already exists.")
        }

        val isExpenseCategory = transactionType == TransactionType.EXPENSE

        // Create a new categoryId
        val newCategory = Category(
            name = name,
            description = description,
            expectedPersonType = expectedPersonType,
            isExpenseCategory = isExpenseCategory,
            color = 0xFFFFFF,
        )


        // Add the new categoryId to the repository
        val result = categoryRepository.insertCategory(newCategory)
        return if (result >= 0) {
            UiState.Success("Category added successfully")
        } else {
            UiState.Error("Unknown error occurred while adding categoryId")
        }
    }
}