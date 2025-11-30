package com.fiscal.compass.presentation.screens.category

import com.fiscal.compass.presentation.model.CategoryUi
import com.fiscal.compass.presentation.model.GroupedCategoryUi
import com.fiscal.compass.presentation.model.TransactionType


sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val message: String) : UiState()
    data class Error(val message: String) : UiState()
}

data class CategoryDialogState(
    val category: CategoryUi? = null
) {
    companion object {
        val Idle = CategoryDialogState()
    }
}


sealed class CategoriesDialog {
    object Hidden : CategoriesDialog()
    object AddCategory : CategoriesDialog()
    object EditCategory : CategoriesDialog()
    object DeleteCategory : CategoriesDialog()
}

data class CategoriesScreenState(
    val uiState: UiState = UiState.Idle,
    val canAdd: Boolean = false,
    val canEdit: Boolean = false,
    val canDelete: Boolean = false,
    val categories: List<CategoryUi> = emptyList(),
    val transactionType: TransactionType = TransactionType.EXPENSE,
    val currentDialog: CategoriesDialog = CategoriesDialog.Hidden,
    val dialogState: CategoryDialogState = CategoryDialogState.Idle
)


