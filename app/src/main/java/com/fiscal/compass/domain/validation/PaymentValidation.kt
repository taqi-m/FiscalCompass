package com.fiscal.compass.domain.validation

import com.fiscal.compass.domain.model.base.Expense
import com.fiscal.compass.domain.model.base.Income

/**
 * Validation utility for transaction payment tracking
 */
object PaymentValidation {

    /**
     * Validates that amountPaid does not exceed the total amount
     * @return Result.success(true) if valid, Result.failure with error message if invalid
     */
    fun validatePaymentAmount(amount: Double, amountPaid: Double): Result<Boolean> {
        return when {
            amountPaid < 0 -> Result.failure(
                IllegalArgumentException("Amount paid cannot be negative")
            )
            amountPaid > amount -> Result.failure(
                IllegalArgumentException("Amount paid ($amountPaid) cannot exceed total amount ($amount)")
            )
            else -> Result.success(true)
        }
    }

    /**
     * Validates income payment amount
     */
    fun validateIncome(income: Income): Result<Boolean> {
        return validatePaymentAmount(income.amount, income.amountPaid)
    }

    /**
     * Validates expense payment amount
     */
    fun validateExpense(expense: Expense): Result<Boolean> {
        return validatePaymentAmount(expense.amount, expense.amountPaid)
    }

    /**
     * Checks if an income is fully received
     */
    fun isFullyReceived(income: Income): Boolean {
        return income.amountPaid >= income.amount
    }

    /**
     * Checks if an expense is fully paid
     */
    fun isFullyPaid(expense: Expense): Boolean {
        return expense.amountPaid >= expense.amount
    }

    /**
     * Checks if an income has partial payment
     */
    fun isPartiallyReceived(income: Income): Boolean {
        return income.amountPaid > 0 && income.amountPaid < income.amount
    }

    /**
     * Checks if an expense has partial payment
     */
    fun isPartiallyPaid(expense: Expense): Boolean {
        return expense.amountPaid > 0 && expense.amountPaid < expense.amount
    }

    /**
     * Gets the outstanding balance for an income
     */
    fun getOutstandingIncome(income: Income): Double {
        return income.amount - income.amountPaid
    }

    /**
     * Gets the outstanding balance for an expense
     */
    fun getOutstandingExpense(expense: Expense): Double {
        return expense.amount - expense.amountPaid
    }

    /**
     * Gets the payment completion percentage
     */
    fun getPaymentProgress(amount: Double, amountPaid: Double): Double {
        if (amount <= 0) return 0.0
        return (amountPaid / amount * 100).coerceIn(0.0, 100.0)
    }

    /**
     * Checks if payment is complete (allowing small floating point differences)
     */
    fun isPaymentComplete(amount: Double, amountPaid: Double, tolerance: Double = 0.01): Boolean {
        return kotlin.math.abs(amount - amountPaid) <= tolerance
    }
}

