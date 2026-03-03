package com.fiscal.compass.presentation.screens.transactionScreens.transactionDetails

import com.fiscal.compass.domain.model.Transaction

sealed class TransactionDetailsEvent {
    data class LoadTransaction(val transaction: Transaction?) : TransactionDetailsEvent()
    data object ShowDeleteDialog : TransactionDetailsEvent()
    data object DismissDeleteDialog : TransactionDetailsEvent()
    data object ConfirmDelete : TransactionDetailsEvent()
}