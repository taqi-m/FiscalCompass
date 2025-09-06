package com.app.uniqueplant.presentation.screens.transactionScreens.addTransaction

import com.app.uniqueplant.domain.model.InputField
import com.app.uniqueplant.presentation.model.GroupedCategoryUi
import com.app.uniqueplant.presentation.model.PersonUi
import com.app.uniqueplant.presentation.model.TransactionType
import com.app.uniqueplant.presentation.screens.categories.UiState

data class GenericInputField <T> (
    val value: T,
    val error: String? = null,
    val isValid: Boolean = true
)

sealed class AddTransactionDialog {
    object Hidden : AddTransactionDialog()
    object DatePicker : AddTransactionDialog()
    object TimePicker : AddTransactionDialog()
}

data class AddTransactionState(
    val uiState: UiState = UiState.Idle,
    val amount: InputField = InputField(
        value = "0.0"
    ),
    val transactionType: TransactionType = TransactionType.EXPENSE,
    val description: InputField = InputField(),
    val formatedDate: String,
    val formatedTime : String,
    val personId: Long? = null,
    val categoryId: Long,
    val subCategoryId: Long? = null,
    val currentDialog: AddTransactionDialog = AddTransactionDialog.Hidden,
    val categories: GroupedCategoryUi = emptyMap(),
    val persons: List<PersonUi> = emptyList()
)