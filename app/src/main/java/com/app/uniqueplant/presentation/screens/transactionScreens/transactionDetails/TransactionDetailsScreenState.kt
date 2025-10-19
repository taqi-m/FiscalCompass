package com.app.uniqueplant.presentation.screens.transactionScreens.transactionDetails

import com.app.uniqueplant.presentation.model.CategoryUi
import com.app.uniqueplant.presentation.model.PersonUi
import com.app.uniqueplant.presentation.model.TransactionUi
import com.app.uniqueplant.presentation.screens.category.UiState

data class TransactionDetailsScreenState(
    val uiState: UiState = UiState.Idle,
    val transaction: TransactionUi? = null,
    val category: CategoryUi? = null,
    val person: PersonUi? = null,
)