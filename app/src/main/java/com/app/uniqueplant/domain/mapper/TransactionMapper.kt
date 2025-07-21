package com.app.uniqueplant.domain.mapper

import com.app.uniqueplant.data.model.Expense
import com.app.uniqueplant.data.model.Income
import com.app.uniqueplant.domain.model.Transaction
import com.app.uniqueplant.domain.model.TransactionType

/**
 * Mapper class to convert between domain Transaction and data layer Expense/Income models
 */
object TransactionMapper {

    /**
     * Maps an Expense to a Transaction
     */
    fun mapExpenseToTransaction(expense: Expense): Transaction {
        return Transaction(
            id = expense.expenseId,
            amount = expense.amount,
            description = expense.description,
            date = expense.date,
            categoryId = expense.categoryId,
            userId = expense.userId,
            type = TransactionType.EXPENSE,
            paymentMethod = expense.paymentMethod,
            location = expense.location,
            receipt = expense.receipt,
            source = null,
            isRecurring = expense.isRecurring,
            recurringFrequency = expense.recurringFrequency,
            isTaxable = null,
            createdAt = expense.createdAt,
            updatedAt = expense.updatedAt
        )
    }

    /**
     * Maps an Income to a Transaction
     */
    fun mapIncomeToTransaction(income: Income): Transaction {
        return Transaction(
            id = income.incomeId,
            amount = income.amount,
            description = income.description,
            date = income.date,
            categoryId = income.categoryId,
            userId = income.userId,
            type = TransactionType.INCOME,
            paymentMethod = null,
            location = null,
            receipt = null,
            source = income.source,
            isRecurring = income.isRecurring,
            recurringFrequency = income.recurringFrequency,
            isTaxable = income.isTaxable,
            createdAt = income.createdAt,
            updatedAt = income.updatedAt
        )
    }

    /**
     * Maps a Transaction to an Expense
     */
    fun mapTransactionToExpense(transaction: Transaction): Expense {
        require(transaction.type == TransactionType.EXPENSE) { "Cannot map INCOME transaction to Expense" }

        return Expense(
            expenseId = transaction.id,
            amount = transaction.amount,
            description = transaction.description,
            date = transaction.date,
            categoryId = transaction.categoryId,
            userId = transaction.userId,
            paymentMethod = transaction.paymentMethod,
            location = transaction.location,
            receipt = transaction.receipt,
            isRecurring = transaction.isRecurring,
            recurringFrequency = transaction.recurringFrequency,
            createdAt = transaction.createdAt,
            updatedAt = transaction.updatedAt
        )
    }

    /**
     * Maps a Transaction to an Income
     */
    fun mapTransactionToIncome(transaction: Transaction): Income {
        require(transaction.type == TransactionType.INCOME) { "Cannot map EXPENSE transaction to Income" }

        return Income(
            incomeId = transaction.id,
            amount = transaction.amount,
            description = transaction.description,
            date = transaction.date,
            categoryId = transaction.categoryId,
            userId = transaction.userId,
            source = transaction.source,
            isRecurring = transaction.isRecurring,
            recurringFrequency = transaction.recurringFrequency,
            isTaxable = transaction.isTaxable ?: true,
            createdAt = transaction.createdAt,
            updatedAt = transaction.updatedAt
        )
    }
}

