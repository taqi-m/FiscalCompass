package com.fiscal.compass.presentation.screens.transactionScreens.transactionDetails

sealed class TransactionDetailsEvent {
    data class OnScreenLoad(val transactionId: Long, val isExpense: Boolean) : TransactionDetailsEvent()
}