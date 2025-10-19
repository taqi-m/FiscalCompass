package com.app.uniqueplant.domain.model.base

import java.util.Date

data class Expense(
    val expenseId: Long = 0,
    val amount: Double,
    val description: String,
    val date: Date,
    val categoryId: Long,
    val userId: String,
    val personId: Long? = null, // Nullable if not associated with a person
    val paymentMethod: String? = null,
    val location: String? = null,
    val receipt: String? = null, // URL to receipt image
    val isRecurring: Boolean = false,
    val recurringFrequency: String? = null, // daily, weekly, monthly, yearly
    val createdAt: Date = Date(System.currentTimeMillis()),
    val updatedAt: Date = Date(System.currentTimeMillis())
)