package com.app.uniqueplant.presentation.screens.categories

import com.app.uniqueplant.presentation.model.CategoryUi
import com.app.uniqueplant.presentation.model.GroupedCategoryUi
import com.app.uniqueplant.presentation.model.TransactionType


sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val message: String) : UiState()
    data class Error(val message: String) : UiState()
}

data class CategoryDialogState(
    val parentId: Long? = null,
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
    val categories: GroupedCategoryUi = emptyMap(),
    val transactionType: TransactionType = TransactionType.EXPENSE,
    val currentDialog: CategoriesDialog = CategoriesDialog.Hidden,
    val dialogState: CategoryDialogState = CategoryDialogState.Idle
)


