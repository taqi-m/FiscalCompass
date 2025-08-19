package com.app.uniqueplant.data.mapper

import com.app.uniqueplant.data.model.IncomeEntity
import com.app.uniqueplant.domain.model.Income
import com.app.uniqueplant.domain.model.Transaction


/**
 * Mapper functions to convert between [IncomeEntity] and [Income].
 * These functions are used to convert data between the database layer and the domain layer.
 */
fun IncomeEntity.toIncome(): Income {
    return Income(
        incomeId = this.incomeId,
        amount = this.amount,
        description = this.description,
        date = this.date,
        categoryId = this.categoryId,
        userId = this.userId,
        personId = this.personId,
        source = this.source,
        isRecurring = this.isRecurring,
        recurringFrequency = this.recurringFrequency,
        isTaxable = this.isTaxable,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun Income.toTransaction(): Transaction {
    return Transaction(
        transactionId = this.incomeId,
        amount = this.amount,
        date = this.date,
        description = this.description,
        isExpense = false,
        transactionType = "Income"
    )
}

fun Income.toIncomeEntity(): IncomeEntity {
    return IncomeEntity(
        incomeId = this.incomeId,
        amount = this.amount,
        description = this.description,
        date = this.date,
        categoryId = this.categoryId,
        userId = this.userId,
        personId = this.personId,
        source = this.source,
        isRecurring = this.isRecurring,
        recurringFrequency = this.recurringFrequency,
        isTaxable = this.isTaxable,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
