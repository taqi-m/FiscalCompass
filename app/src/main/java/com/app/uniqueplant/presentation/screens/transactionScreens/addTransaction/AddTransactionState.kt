package com.app.uniqueplant.presentation.screens.transactionScreens.addTransaction

import com.app.uniqueplant.presentation.model.GroupedCategoryUi
import com.app.uniqueplant.presentation.model.InputField
import com.app.uniqueplant.presentation.model.PersonUi
import com.app.uniqueplant.presentation.model.TransactionType
import com.app.uniqueplant.presentation.screens.category.UiState
import java.util.Calendar

data class GenericInputField <T> (
    val value: T,
    val error: String? = null,
    val isValid: Boolean = true
)

data class AddTransactionState(
    val uiState: UiState = UiState.Idle,
    val amount: InputField = InputField(
        value = "0.0"
    ),
    val transactionType: TransactionType = TransactionType.EXPENSE,
    val description: InputField = InputField(),
    val selectedDate: Long? = null,
    val selectedTime: Calendar? = null,
    val personId: Long? = null,
    val categoryId: Long,
    val subCategoryId: Long? = null,
    val categories: GroupedCategoryUi = emptyMap(),
    val persons: List<PersonUi> = emptyList()
)