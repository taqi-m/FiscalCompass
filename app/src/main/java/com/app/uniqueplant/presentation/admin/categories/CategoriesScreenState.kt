package com.app.uniqueplant.presentation.admin.categories

import com.app.uniqueplant.domain.model.Category
import com.app.uniqueplant.presentation.model.TransactionType


sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val message: String) : UiState()
    data class Error(val message: String) : UiState()
}

data class CategoryDialogState(
    val category: Category? = null
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
//    val isLoading: Boolean = false,
//    val error: String? = null,
//    val successMessage: String? = null,
    val categories: List<Category> = emptyList(),
    val transactionType: TransactionType = TransactionType.EXPENSE,
    val currentDialog: CategoriesDialog = CategoriesDialog.Hidden,
    val dialogState: CategoryDialogState = CategoryDialogState.Idle
)


