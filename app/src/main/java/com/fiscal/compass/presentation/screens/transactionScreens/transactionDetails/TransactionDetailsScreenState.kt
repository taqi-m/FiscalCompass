package com.fiscal.compass.presentation.screens.transactionScreens.transactionDetails

import com.fiscal.compass.presentation.model.CategoryUi
import com.fiscal.compass.presentation.model.PersonUi
import com.fiscal.compass.presentation.model.TransactionUi
import com.fiscal.compass.presentation.screens.category.UiState

data class TransactionDetailsScreenState(
    val uiState: UiState = UiState.Idle,
    val transaction: TransactionUi? = null,
    val category: CategoryUi? = null,
    val person: PersonUi? = null,
)