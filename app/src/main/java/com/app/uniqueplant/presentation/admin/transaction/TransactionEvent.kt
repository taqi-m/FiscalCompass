package com.app.uniqueplant.presentation.admin.transaction

sealed class TransactionEvent {
    data class OnTransactionSelected(val transaction: Any) : TransactionEvent()
    data class OnTransactionEdited(val transaction: Any) : TransactionEvent()
    data class OnTransactionAdded(val transaction: Any) : TransactionEvent()
    object OnDialogToggle : TransactionEvent()
    object OnResetClicked : TransactionEvent()
    object OnSaveClicked : TransactionEvent()
    object OnDeleteModeToggle : TransactionEvent()
    object OnEditModeToggle : TransactionEvent()
    object OnTransactionDeleted : TransactionEvent()
}