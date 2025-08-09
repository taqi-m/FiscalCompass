package com.app.uniqueplant.presentation.admin.categories

import com.app.uniqueplant.data.model.Category
import com.app.uniqueplant.domain.model.TransactionType


sealed class CategoryDialogToggle {
    object Add : CategoryDialogToggle()
    data class Edit(val category: Category) : CategoryDialogToggle()
    data class Delete(val category: Category) : CategoryDialogToggle()
    object Hidden : CategoryDialogToggle()
}

sealed class CategoryDialogSubmit {
    data class Add(val name: String, val description: String, val expectedPersonType: String) : CategoryDialogSubmit()
    data class Edit(val category: Category) : CategoryDialogSubmit()

    object Delete : CategoryDialogSubmit()
}



sealed class CategoriesEvent {
    object OnUiReset : CategoriesEvent()
    data class OnTransactionTypeChange(val transactionType: TransactionType) : CategoriesEvent()
    data class OnCategoryDialogToggle(val event: CategoryDialogToggle) : CategoriesEvent()
    data class OnCategoryDialogSubmit(val event: CategoryDialogSubmit) : CategoriesEvent()
    data class OnCategoryDialogStateChange(val state: CategoryDialogState) : CategoriesEvent()
}