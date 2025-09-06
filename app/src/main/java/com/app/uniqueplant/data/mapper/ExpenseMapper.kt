package com.app.uniqueplant.data.mapper

import com.app.uniqueplant.data.model.ExpenseEntity
import com.app.uniqueplant.domain.model.Expense
import com.app.uniqueplant.domain.model.Transaction

/**
 * Mapper functions to convert between [ExpenseEntity] and [Expense].
 * These functions are used to convert data between the database layer and the domain layer.
 */
fun ExpenseEntity.toDomain(): Expense {
    return Expense(
        expenseId = this.expenseId,
        amount = this.amount,
        description = this.description,
        date = this.date,
        categoryId = this.categoryId,
        userId = this.userId,
        personId = this.personId,
        paymentMethod = this.paymentMethod,
        location = this.location,
        receipt = this.receipt,
        isRecurring = this.isRecurring,
        recurringFrequency = this.recurringFrequency,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
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

fun Expense.toExpenseEntity(): ExpenseEntity {
    return ExpenseEntity(
        expenseId = this.expenseId,
        amount = this.amount,
        description = this.description,
        date = this.date,
        categoryId = this.categoryId,
        userId = this.userId,
        personId = this.personId,
        paymentMethod = this.paymentMethod,
        location = this.location,
        receipt = this.receipt,
        isRecurring = this.isRecurring,
        recurringFrequency = this.recurringFrequency,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
