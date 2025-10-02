package com.app.uniqueplant.data.mappers

import com.app.uniqueplant.data.local.model.IncomeEntity
import com.app.uniqueplant.domain.model.Income
import com.app.uniqueplant.domain.model.Transaction
import java.util.Date


/**
 * Mapper functions to convert between [IncomeEntity] and [Income].
 * These functions are used to convert data between the database layer and the domain layer.
 */
fun IncomeEntity.toDomain(): Income {
    return Income(
        incomeId = this.incomeId,
        amount = this.amount,
        description = this.description,
        date = Date(this.date),
        categoryId = this.categoryId,
        userId = this.userId,
        personId = this.personId,
        source = this.source,
        isRecurring = this.isRecurring,
        recurringFrequency = this.recurringFrequency,
        isTaxable = this.isTaxable,
        createdAt = Date(this.createdAt),
        updatedAt = Date(this.updatedAt)
    )
}

fun Income.toTransaction(): Transaction {
    return Transaction(
        transactionId = this.incomeId,
        amount = this.amount,
        categoryId = this.categoryId,
        personId = this.personId,
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
        date = this.date.time,
        categoryId = this.categoryId,
        userId = this.userId,
        personId = this.personId,
        source = this.source,
        isRecurring = this.isRecurring,
        recurringFrequency = this.recurringFrequency,
        isTaxable = this.isTaxable,
        createdAt = this.createdAt.time,
        updatedAt = this.updatedAt.time
    )
}
