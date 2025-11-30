package com.fiscal.compass.domain.usecase.expense

import com.fiscal.compass.domain.repository.ExpenseRepository
import com.fiscal.compass.domain.validation.PaymentValidation
import javax.inject.Inject

/**
 * Use case for updating the paid amount of an expense transaction
 */
class UpdateExpensePaymentUC @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    suspend operator fun invoke(expenseId: Long, newAmountPaid: Double): Result<Unit> {
        return try {
            // Get the existing expense
            val expense = expenseRepository.getExpenseById(expenseId)
                ?: return Result.failure(IllegalArgumentException("Expense not found with ID: $expenseId"))

            // Validate the new payment amount
            PaymentValidation.validatePaymentAmount(expense.amount, newAmountPaid).getOrElse {
                return Result.failure(it)
            }

            // Update the expense with new amountPaid
            val updatedExpense = expense.copy(
                amountPaid = newAmountPaid,
                updatedAt = java.util.Date()
            )

            expenseRepository.updateExpense(updatedExpense)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Add a payment to the existing amount paid
     */
    suspend fun addPayment(expenseId: Long, paymentAmount: Double): Result<Unit> {
        return try {
            if (paymentAmount <= 0) {
                return Result.failure(IllegalArgumentException("Payment amount must be greater than zero"))
            }

            val expense = expenseRepository.getExpenseById(expenseId)
                ?: return Result.failure(IllegalArgumentException("Expense not found with ID: $expenseId"))

            val newAmountPaid = expense.amountPaid + paymentAmount

            // Validate the new total payment amount
            PaymentValidation.validatePaymentAmount(expense.amount, newAmountPaid).getOrElse {
                return Result.failure(it)
            }

            invoke(expenseId, newAmountPaid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mark an expense as fully paid
     */
    suspend fun markAsFullyPaid(expenseId: Long): Result<Unit> {
        return try {
            val expense = expenseRepository.getExpenseById(expenseId)
                ?: return Result.failure(IllegalArgumentException("Expense not found with ID: $expenseId"))

            invoke(expenseId, expense.amount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

