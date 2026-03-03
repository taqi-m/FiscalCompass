package com.fiscal.compass.domain.model.base

import java.util.Date

data class Income(
    val incomeId: String,
    val amount: Double,
    val amountPaid: Double = 0.0,
    val description: String,
    val date: Date,
    val categoryId: String,
    val userId: String,
    val personId: String? = null,
    val source: String? = null,
    val isRecurring: Boolean = false,
    val recurringFrequency: String? = null,
    val isTaxable: Boolean = true,
    val createdAt: Date = Date(System.currentTimeMillis()),
    val updatedAt: Date = Date(System.currentTimeMillis())
)