package com.app.uniqueplant.presentation.admin.transaction

import com.app.uniqueplant.domain.model.Transaction

sealed class TransactionEvent {
    data class OnTransactionSelected(val transaction: Transaction) : TransactionEvent()
    data class OnTransactionEdited(val transaction: Transaction) : TransactionEvent()
    data class OnTransactionAdded(val transaction: Transaction) : TransactionEvent()
    object OnDialogToggle : TransactionEvent()
    object OnResetClicked : TransactionEvent()
    object OnSaveClicked : TransactionEvent()
    object OnDeleteModeToggle : TransactionEvent()
    object OnEditModeToggle : TransactionEvent()
    object OnTransactionDeleted : TransactionEvent()
}