package com.fiscal.compass.domain.usecase.income

import com.fiscal.compass.domain.repository.IncomeRepository
import com.fiscal.compass.domain.validation.PaymentValidation
import javax.inject.Inject

/**
 * Use case for updating the paid amount of an income transaction
 */
class UpdateIncomePaymentUC @Inject constructor(
    private val incomeRepository: IncomeRepository
) {
    suspend operator fun invoke(incomeId: Long, newAmountPaid: Double): Result<Unit> {
        return try {
            // Get the existing income
            val income = incomeRepository.getIncomeById(incomeId)
                ?: return Result.failure(IllegalArgumentException("Income not found with ID: $incomeId"))

            // Validate the new payment amount
            PaymentValidation.validatePaymentAmount(income.amount, newAmountPaid).getOrElse {
                return Result.failure(it)
            }

            // Update the income with new amountPaid
            val updatedIncome = income.copy(
                amountPaid = newAmountPaid,
                updatedAt = java.util.Date()
            )

            incomeRepository.updateIncome(updatedIncome)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Add a payment to the existing amount paid
     */
    suspend fun addPayment(incomeId: Long, paymentAmount: Double): Result<Unit> {
        return try {
            if (paymentAmount <= 0) {
                return Result.failure(IllegalArgumentException("Payment amount must be greater than zero"))
            }

            val income = incomeRepository.getIncomeById(incomeId)
                ?: return Result.failure(IllegalArgumentException("Income not found with ID: $incomeId"))

            val newAmountPaid = income.amountPaid + paymentAmount

            // Validate the new total payment amount
            PaymentValidation.validatePaymentAmount(income.amount, newAmountPaid).getOrElse {
                return Result.failure(it)
            }

            invoke(incomeId, newAmountPaid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mark an income as fully received
     */
    suspend fun markAsFullyReceived(incomeId: Long): Result<Unit> {
        return try {
            val income = incomeRepository.getIncomeById(incomeId)
                ?: return Result.failure(IllegalArgumentException("Income not found with ID: $incomeId"))

            invoke(incomeId, income.amount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

