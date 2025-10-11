package com.app.uniqueplant.presentation.screens.transactionScreens.viewTransactions

import com.app.uniqueplant.presentation.model.TransactionUi
import com.app.uniqueplant.presentation.screens.categories.UiState
import java.util.Date


data class TransactionDialogState(
    val transaction: TransactionUi? = null
) {
    companion object {
        val Idle = TransactionDialogState()
    }
}

sealed class TransactionScreenDialog {
    object Hidden : TransactionScreenDialog()
    object EditTransaction : TransactionScreenDialog()
    object DeleteTransaction : TransactionScreenDialog()
}

data class TransactionScreenState (
    val uiState: UiState = UiState.Idle,
    val currentDate: Date = Date(),
    val transactions: Map<Date, List<TransactionUi>> = emptyMap(),
    val incoming: Double = 0.0,
    val outgoing: Double = 0.0,
    val currentDialog: TransactionScreenDialog = TransactionScreenDialog.Hidden,
    val dialogState: TransactionDialogState = TransactionDialogState.Idle,
)