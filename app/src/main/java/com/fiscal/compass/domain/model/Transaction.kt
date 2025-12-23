package com.fiscal.compass.domain.model

import com.fiscal.compass.domain.model.base.Category
import com.fiscal.compass.domain.model.base.Person
import java.util.Date

data class Transaction(
    val transactionId: Long,
    val amount: Double,
    val amountPaid: Double = 0.0,
    val categoryId: Long,
    val category: Category? = null,
    val personId: Long? = null,
    val person: Person? = null,
    val date: Date,
    val description: String? = null,
    val isExpense: Boolean,
    val transactionType: String,
) {
    companion object {
        fun default(): Transaction = Transaction(
            transactionId = 1L,
            amount = 100.0,
            amountPaid = 0.0,
            categoryId = 1L,
            category = null,
            personId = null,
            person = null,
            date = Date(),
            description = "Default transaction",
            isExpense = true,
            transactionType = "EXPENSE"
        )

        fun empty(): Transaction = Transaction(
            transactionId = 0L,
            amount = 0.0,
            amountPaid = 0.0,
            categoryId = 0L,
            category = null,
            personId = null,
            person = null,
            date = Date(),
            description = null,
            isExpense = true,
            transactionType = "EXPENSE"
        )

        fun nullTransaction(): Transaction = Transaction(
            transactionId = 0L,
            amount = 0.0,
            amountPaid = 0.0,
            categoryId = 0L,
            category = null,
            personId = null,
            person = null,
            date = Date(),
            description = null,
            isExpense = true,
            transactionType = "NONE"
        )

        fun sampleExpense(): Transaction = Transaction(
            transactionId = 101L,
            amount = 45.50,
            amountPaid = 45.50,
            categoryId = 2L,
            category = null,
            personId = null,
            person = null,
            date = Date(),
            description = "Lunch at cafe",
            isExpense = true,
            transactionType = "CARD"
        )

        fun sampleIncome(): Transaction = Transaction(
            transactionId = 102L,
            amount = 1500.0,
            amountPaid = 1500.0,
            categoryId = 3L,
            category = null,
            personId = null,
            person = null,
            date = Date(),
            description = "Monthly salary",
            isExpense = false,
            transactionType = "BANK"
        )

        fun sampleList(): List<Transaction> = listOf(sampleExpense(), sampleIncome(), default(), empty())
    }
}