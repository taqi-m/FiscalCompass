package com.app.uniqueplant.domain.model

import java.util.Date

data class Transaction(
    val transactionId: Long,
    val amount: Double,
    val date: Date,
    val description: String? = null,
    val isExpense: Boolean,
    val transactionType: String
)
