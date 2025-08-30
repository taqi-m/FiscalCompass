package com.app.uniqueplant.presentation.screens.viewTransactions

import com.app.uniqueplant.presentation.model.TransactionUi

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
    data class OnTransactionDialogToggle(val event: TransactionDialogToggle) : TransactionEvent()
    data class OnTransactionDialogSubmit(val event: TransactionDialogSubmit) : TransactionEvent()
    data class OnTransactionDialogStateChange(val state: TransactionDialogState) : TransactionEvent()
}