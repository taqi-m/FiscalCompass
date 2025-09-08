package com.app.uniqueplant.data.mapper

import com.app.uniqueplant.data.model.ExpenseEntity
import com.app.uniqueplant.domain.model.Expense
import com.app.uniqueplant.domain.model.Transaction
import com.google.firebase.Timestamp
import java.util.Date

/**
 * Mapper functions to convert between [ExpenseEntity] and [Expense].
 * These functions are used to convert data between the database layer and the domain layer.
 */
fun ExpenseEntity.toDomain(): Expense {
    return Expense(
        expenseId = this.expenseId,
        amount = this.amount,
        description = this.description,
        date = Date(this.date),
        categoryId = this.categoryId,
        userId = this.userId,
        personId = this.personId,
        paymentMethod = this.paymentMethod,
        location = this.location,
        receipt = this.receipt,
        isRecurring = this.isRecurring,
        recurringFrequency = this.recurringFrequency,
        createdAt = Date(this.createdAt),
        updatedAt = Date(this.updatedAt)
    )
}

fun ExpenseEntity.toRemote(): Map<String, Any?> {
    return mapOf(
        "expenseId" to this.expenseId,
        "amount" to this.amount,
        "description" to this.description,
        "date" to Timestamp(Date(this.date)),
        "categoryId" to this.categoryId,
        "userId" to this.userId,
        "personId" to this.personId,
        "paymentMethod" to this.paymentMethod,
        "location" to this.location,
        "receipt" to this.receipt,
        "isRecurring" to this.isRecurring,
        "recurringFrequency" to this.recurringFrequency,
        "createdAt" to Timestamp(Date(this.createdAt)),
        "updatedAt" to Timestamp(Date(this.updatedAt))
    )
}

fun Expense.toTransaction(): Transaction {
    return Transaction(
        transactionId = this.expenseId,
        amount = this.amount,
        categoryId = this.categoryId,
        personId = this.personId,
        date = this.date,
        description = this.description,
        isExpense = true,
        transactionType = "Expense"
    )
}

fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        expenseId = this.expenseId,
        amount = this.amount,
        description = this.description,
        date = this.date.time,
        categoryId = this.categoryId,
        userId = this.userId,
        personId = this.personId,
        paymentMethod = this.paymentMethod,
        location = this.location,
        receipt = this.receipt,
        isRecurring = this.isRecurring,
        recurringFrequency = this.recurringFrequency,
        createdAt = this.createdAt.time,
        updatedAt = this.updatedAt.time
    )
}
