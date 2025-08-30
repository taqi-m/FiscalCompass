package com.app.uniqueplant.presentation.screens.addTransaction

import com.app.uniqueplant.domain.model.InputField
import com.app.uniqueplant.presentation.model.GroupedCategoryUi
import com.app.uniqueplant.presentation.model.TransactionType
import java.util.Date

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
    val amount: InputField = InputField(
        value = "0.0"
    ),
    val transactionType: TransactionType = TransactionType.EXPENSE,
    val description: InputField = InputField(),
    val date: Date,
    val categoryId: Long,
    val subCategoryId: Long? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val message: String = "",
    val currentDialog: AddTransactionDialog = AddTransactionDialog.Hidden,
    val categories: GroupedCategoryUi = emptyMap()
)