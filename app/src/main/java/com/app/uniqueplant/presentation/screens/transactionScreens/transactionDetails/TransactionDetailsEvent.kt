package com.app.uniqueplant.presentation.screens.transactionScreens.transactionDetails

sealed class TransactionDetailsEvent {
    data class OnScreenLoad(val transactionId: Long, val isExpense: Boolean) : TransactionDetailsEvent()
}