package com.fiscal.compass.presentation.screens.transactionScreens.amountScreen

import com.fiscal.compass.domain.model.Transaction
import com.fiscal.compass.presentation.model.InputField
import com.fiscal.compass.presentation.model.TransactionUi
import com.fiscal.compass.presentation.screens.category.UiState

data class AmountScreenState(
    val uiState: UiState = UiState.Idle,
    val editMode: Boolean = false,
    val transaction: Transaction = Transaction.nullTransaction(),
    val totalAmount: Double = 0.0,
    val paidAmount: Double = 0.0,
)