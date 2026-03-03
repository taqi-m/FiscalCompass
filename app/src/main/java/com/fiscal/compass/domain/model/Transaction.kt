package com.fiscal.compass.domain.model

import com.fiscal.compass.domain.model.base.Category
import com.fiscal.compass.domain.model.base.Person
import com.fiscal.compass.domain.util.DateTimeUtil.getCurrentDate
import com.fiscal.compass.domain.util.TransactionType
import java.util.Date

data class Transaction(
    val transactionId: String,
    val amount: Double,
    val amountPaid: Double = 0.0,
    val categoryId: String,
    val category: Category? = null,
    val personId: String? = null,
    val person: Person? = null,
    val date: Date,
    val description: String? = null,
    val isExpense: Boolean,
    val transactionType: String,
) {
    companion object {
        fun default(): Transaction = Transaction(
            transactionId = "1",
            amount = 100.0,
            amountPaid = 0.0,
            categoryId = "1",
            category = null,
            personId = null,
            person = null,
            date = getCurrentDate(),
            description = "Default transaction",
            isExpense = false,
            transactionType = TransactionType.entries.first().name
        )

        fun empty(): Transaction = Transaction(
            transactionId = "",
            amount = 0.0,
            amountPaid = 0.0,
            categoryId = "",
            category = null,
            personId = null,
            person = null,
            date = getCurrentDate(),
            description = null,
            isExpense = false,
            transactionType = TransactionType.entries.first().name
        )

        fun nullTransaction(): Transaction = Transaction(
            transactionId = "",
            amount = 0.0,
            amountPaid = 0.0,
            categoryId = "",
            category = null,
            personId = null,
            person = null,
            date = getCurrentDate(),
            description = null,
            isExpense = true,
            transactionType = "NONE"
        )

        fun sampleExpense(): Transaction = Transaction(
            transactionId = "101",
            amount = 45.50,
            amountPaid = 45.50,
            categoryId = "2",
            category = null,
            personId = null,
            person = null,
            date = getCurrentDate(),
            description = "Lunch at cafe",
            isExpense = true,
            transactionType = "CARD"
        )

        fun sampleIncome(): Transaction = Transaction(
            transactionId = "102",
            amount = 1500.0,
            amountPaid = 1500.0,
            categoryId = "3",
            category = null,
            personId = null,
            person = null,
            date = getCurrentDate(),
            description = "Monthly salary",
            isExpense = false,
            transactionType = "BANK"
        )

        fun sampleList(): List<Transaction> = listOf(sampleExpense(), sampleIncome(), default(), empty())
    }
}