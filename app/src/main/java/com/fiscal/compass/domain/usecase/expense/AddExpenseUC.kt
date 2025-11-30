package com.fiscal.compass.domain.usecase.expense

import com.fiscal.compass.domain.model.base.Expense
import com.fiscal.compass.domain.repository.ExpenseRepository
import com.fiscal.compass.domain.usecase.auth.SessionUseCase
import com.fiscal.compass.domain.validation.PaymentValidation
import java.util.Date

class AddExpenseUC(
    private val sessionUseCase: SessionUseCase,
    private val expenseRepository: ExpenseRepository
) {
    suspend fun addExpense(
        amount: Double,
        categoryId: Long,
        description: String,
        date: Date,
        amountPaid: Double = 0.0
    ): Result<Unit> {
        return try {
            val uid: String? = sessionUseCase.getCurrentUser()?.uid
            if (uid.isNullOrEmpty()) {
                return Result.failure(IllegalStateException("User is not logged in"))
            }

            // Validate payment amount
            PaymentValidation.validatePaymentAmount(amount, amountPaid).getOrElse {
                return Result.failure(it)
            }

            val newExpense = Expense(
                amount = amount,
                amountPaid = amountPaid,
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