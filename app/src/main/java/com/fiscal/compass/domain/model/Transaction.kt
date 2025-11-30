package com.fiscal.compass.domain.model

import com.fiscal.compass.domain.model.base.Category
import com.fiscal.compass.domain.model.base.Person
import java.util.Date

data class Transaction(
    val transactionId: Long,
    val amount: Double,
    val amountPaid: Double = 0.0,
    val categoryId: Long,
    val category : Category? = null,
    val personId: Long? = null,
    val person: Person? = null,
    val date: Date,
    val description: String? = null,
    val isExpense: Boolean,
    val transactionType: String
)
