package com.fiscal.compass.domain.usecase.expense

import com.fiscal.compass.domain.model.base.Expense
import com.fiscal.compass.domain.repository.ExpenseRepository
import com.fiscal.compass.domain.usecase.auth.SessionUseCase
import java.util.Date

class AddExpenseUC(
    private val sessionUseCase: SessionUseCase,
    private val expenseRepository: ExpenseRepository
) {
    suspend fun addExpense(
        amount: Double,
        categoryId: Long,
        description: String,
        date: Date
    ): Result<Unit> {
        return try {
            val uid: String? = sessionUseCase.getCurrentUser()?.uid
            if (uid.isNullOrEmpty()) {
                return Result.failure(IllegalStateException("User is not logged in"))
            }

            val newExpense = Expense(
                amount = amount,
                description = description,
                date = date,
                categoryId = categoryId,
                userId = uid
            )


            expenseRepository.addExpense(newExpense)
            Result.success(Unit) // Placeholder for successful operation
        } catch (e: Exception) {
            Result.failure(e) // Handle any exceptions that occur
        }
    }
}