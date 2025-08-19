package com.app.uniqueplant.presentation.model

data class TransactionUi(
    val transactionId: Long,
    val formatedAmount: String,
    val formatedDate: String = "",
    val formatedTime: String,
    val description: String? = null,
    val isExpense: Boolean,
    val transactionType : String,
)