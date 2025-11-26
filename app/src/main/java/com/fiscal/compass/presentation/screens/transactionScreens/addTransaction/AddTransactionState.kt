package com.fiscal.compass.presentation.screens.transactionScreens.addTransaction

import com.fiscal.compass.presentation.model.GroupedCategoryUi
import com.fiscal.compass.presentation.model.InputField
import com.fiscal.compass.presentation.model.PersonUi
import com.fiscal.compass.presentation.model.TransactionType
import com.fiscal.compass.presentation.screens.category.UiState
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