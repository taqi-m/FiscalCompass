package com.fiscal.compass.presentation.screens.transactionScreens.viewTransactions

import com.fiscal.compass.presentation.model.TransactionUi

sealed class TransactionDialogToggle {
    data class Edit(val transaction: TransactionUi) : TransactionDialogToggle()
    data class Delete(val transaction: TransactionUi) : TransactionDialogToggle()
    object Hidden : TransactionDialogToggle()
}

sealed class TransactionDialogSubmit {
    data class Edit(val transaction: TransactionUi) : TransactionDialogSubmit()
    object Delete : TransactionDialogSubmit()
}

sealed class TransactionEvent {
    object OnUiReset : TransactionEvent()
    object OnPreviousMonth : TransactionEvent()
    object OnNextMonth : TransactionEvent()
    data class OnTransactionSelected(val transaction: TransactionUi) : TransactionEvent()
    data class OnTransactionDialogToggle(val event: TransactionDialogToggle) : TransactionEvent()
    data class OnTransactionDialogSubmit(val event: TransactionDialogSubmit) : TransactionEvent()
    data class OnTransactionDialogStateChange(val state: TransactionDialogState) : TransactionEvent()
}