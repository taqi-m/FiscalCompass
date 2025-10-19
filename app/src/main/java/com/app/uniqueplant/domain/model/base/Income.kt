package com.app.uniqueplant.domain.model.base

import java.util.Date

data class Income(
    val incomeId: Long = 0,
    val amount: Double,
    val description: String,
    val date: Date,
    val categoryId: Long,
    val userId: String,
    val personId: Long? = null,
    val source: String? = null,
    val isRecurring: Boolean = false,
    val recurringFrequency: String? = null,
    val isTaxable: Boolean = true,
    val createdAt: Date = Date(System.currentTimeMillis()),
    val updatedAt: Date = Date(System.currentTimeMillis())
)