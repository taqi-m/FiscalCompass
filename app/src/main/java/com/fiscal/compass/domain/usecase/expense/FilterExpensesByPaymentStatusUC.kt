package com.fiscal.compass.domain.usecase.expense

import com.fiscal.compass.domain.model.base.Expense
import com.fiscal.compass.domain.repository.ExpenseRepository
import com.fiscal.compass.domain.validation.PaymentValidation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case for filtering expenses by payment status
 */
class FilterExpensesByPaymentStatusUC @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    /**
     * Get all fully paid expenses for a user
     */
    suspend fun getFullyPaidExpenses(userId: String): Flow<List<Expense>> {
        return expenseRepository.getExpensesByUser(userId).map { expenses ->
            expenses.filter { PaymentValidation.isFullyPaid(it) }
        }
    }

    /**
     * Get all partially paid expenses for a user
     */
    suspend fun getPartiallyPaidExpenses(userId: String): Flow<List<Expense>> {
        return expenseRepository.getExpensesByUser(userId).map { expenses ->
            expenses.filter { PaymentValidation.isPartiallyPaid(it) }
        }
    }

    /**
     * Get all unpaid expenses (amountPaid = 0) for a user
     */
    suspend fun getUnpaidExpenses(userId: String): Flow<List<Expense>> {
        return expenseRepository.getExpensesByUser(userId).map { expenses ->
            expenses.filter { it.amountPaid == 0.0 }
        }
    }

    /**
     * Get all pending expenses (not fully paid) for a user
     */
    suspend fun getPendingExpenses(userId: String): Flow<List<Expense>> {
        return expenseRepository.getExpensesByUser(userId).map { expenses ->
            expenses.filter { !PaymentValidation.isFullyPaid(it) }
        }
    }

    /**
     * Calculate total outstanding expense amount for a user
     */
    suspend fun getTotalOutstandingExpense(userId: String): Flow<Double> {
        return expenseRepository.getExpensesByUser(userId).map { expenses ->
            expenses.sumOf { PaymentValidation.getOutstandingExpense(it) }
        }
    }

    /**
     * Calculate total paid amount for a user
     */
    suspend fun getTotalPaidAmount(userId: String): Flow<Double> {
        return expenseRepository.getExpensesByUser(userId).map { expenses ->
            expenses.sumOf { it.amountPaid }
        }
    }
}

