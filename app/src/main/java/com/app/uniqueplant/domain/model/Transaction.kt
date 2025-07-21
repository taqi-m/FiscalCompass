package com.app.uniqueplant.domain.model

import java.util.Date

enum class TransactionType {
    EXPENSE,
    INCOME
}

data class Transaction(
    val id: Long,
    val amount: Double,
    val description: String,
    val date: Date,
    val categoryId: Long,
    val userId: String,
    val type: TransactionType,
    val paymentMethod: String? = null,
    val location: String? = null,
    val receipt: String? = null,
    val source: String? = null,
    val isRecurring: Boolean = false,
    val recurringFrequency: String? = null,
    val isTaxable: Boolean? = null,
    val createdAt: Date,
    val updatedAt: Date
)
