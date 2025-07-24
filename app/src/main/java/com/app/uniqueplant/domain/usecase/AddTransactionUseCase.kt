package com.app.uniqueplant.domain.usecase

import com.app.uniqueplant.data.model.Expense
import com.app.uniqueplant.data.model.Income
import com.app.uniqueplant.domain.model.TransactionType
import com.app.uniqueplant.domain.repository.ExpenseRepository
import com.app.uniqueplant.domain.repository.IncomeRepository
import com.app.uniqueplant.domain.usecase.auth.SessionUseCase
import java.util.Date

class AddTransactionUseCase(
    private val sessionUseCase: SessionUseCase,
    private val incomeRepository: IncomeRepository,
    private val expenseRepository: ExpenseRepository
) {
    suspend fun addTransaction(
        amount: Double,
        categoryId: Long,
        description: String,
        date: Date,
        transactionType: TransactionType
    ): Result<Long> {
        return try {
            val uid = sessionUseCase.getCurrentUser()?.uid
                ?: return Result.failure(IllegalStateException("User is not logged in"))

            if (transactionType == TransactionType.INCOME) {
                val newIncome = Income(
                    amount = amount,
                    description = description,
                    date = date,
                    categoryId = categoryId,
                    userId = uid
                )
                Result.success(incomeRepository.addIncome(newIncome))
            } else {
                val newExpense = Expense(
                    amount = amount,
                    description = description,
                    date = date,
                    categoryId = categoryId,
                    userId = uid
                )
                Result.success(expenseRepository.addExpense(newExpense))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}