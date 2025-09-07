package com.app.uniqueplant.domain.usecase.categories

import com.app.uniqueplant.domain.model.Category
import com.app.uniqueplant.domain.repository.CategoryRepository
import com.app.uniqueplant.presentation.model.TransactionType
import com.app.uniqueplant.presentation.screens.categories.UiState
import javax.inject.Inject

class AddCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend fun invoke(name: String, parentId: Long?, description: String, transactionType: TransactionType, expectedPersonType: String): UiState {
        // Check if the categoryId already exists
        val existingCategory = categoryRepository.getCategoryByName(name)
        if (existingCategory != null) {
            return UiState.Error("Category with name $name already exists.")
        }

        val isExpenseCategory = transactionType == TransactionType.EXPENSE

        // Create a new categoryId
        val newCategory = Category(
            parentCategoryId = parentId,
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