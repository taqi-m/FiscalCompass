package com.app.uniqueplant.domain.usecase.expense

import android.util.Log
import com.app.uniqueplant.domain.model.Expense
import com.app.uniqueplant.domain.repository.ExpenseRepository
import com.app.uniqueplant.domain.usecase.auth.SessionUseCase
import java.util.Date

class AddExpenseUseCase(
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

            Log.d(TAG, "Adding income: $newExpense")


            expenseRepository.addExpense(newExpense)
            Result.success(Unit) // Placeholder for successful operation
        } catch (e: Exception) {
            Result.failure(e) // Handle any exceptions that occur
        }
    }

    companion object {
        const val TAG = "AddExpenseUseCase"
    }
}