package com.fiscal.compass.presentation.screens.transactionScreens.transactionDetails

import com.fiscal.compass.domain.model.Transaction

sealed class TransactionDetailsEvent {
    data class LoadTransaction(val transaction: Transaction?) : TransactionDetailsEvent()
}