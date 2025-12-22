package com.fiscal.compass.domain.service

import com.fiscal.compass.domain.model.base.Expense
import com.fiscal.compass.domain.model.ExpenseFull
import com.fiscal.compass.domain.repository.ExpenseRepository
import com.fiscal.compass.domain.usecase.auth.SessionUseCase
import com.fiscal.compass.domain.validation.PaymentValidation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class ExpenseServiceImpl @Inject constructor(
    private val sessionUseCase: SessionUseCase,
    private val expenseRepository: ExpenseRepository
) : ExpenseService {

    override suspend fun addExpense(
        amount: Double,
        categoryId: Long,
        description: String,
        date: Date,
        amountPaid: Double
    ): Result<Unit> {
        return try {
            val uid: String? = sessionUseCase.getCurrentUser()?.uid
            if (uid.isNullOrEmpty()) {
                return Result.failure(IllegalStateException("User is not logged in"))
            }

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
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserExpenses(userId: String): Flow<List<Expense>> {
        return expenseRepository.getExpensesByUser(userId)
    }

    override suspend fun getExpenseWithCategoryAndPerson(id: Long): ExpenseFull {
        val expense = expenseRepository.getSingleFulExpenseById(id)
            ?: throw IllegalArgumentException("Expense not found")
        return expense
    }

    override suspend fun updateExpensePayment(expenseId: Long, newAmountPaid: Double): Result<Unit> {
        return try {
            val expense = expenseRepository.getExpenseById(expenseId)
                ?: return Result.failure(IllegalArgumentException("Expense not found with ID: $expenseId"))

            PaymentValidation.validatePaymentAmount(expense.amount, newAmountPaid).getOrElse {
                return Result.failure(it)
            }

            val updatedExpense = expense.copy(
                amountPaid = newAmountPaid,
                updatedAt = Date()
            )

            expenseRepository.updateExpense(updatedExpense)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addPayment(expenseId: Long, paymentAmount: Double): Result<Unit> {
        return try {
            if (paymentAmount <= 0) {
                return Result.failure(IllegalArgumentException("Payment amount must be greater than zero"))
            }

            val expense = expenseRepository.getExpenseById(expenseId)
                ?: return Result.failure(IllegalArgumentException("Expense not found with ID: $expenseId"))

            val newAmountPaid = expense.amountPaid + paymentAmount

            PaymentValidation.validatePaymentAmount(expense.amount, newAmountPaid).getOrElse {
                return Result.failure(it)
            }

            updateExpensePayment(expenseId, newAmountPaid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAsFullyPaid(expenseId: Long): Result<Unit> {
        return try {
            val expense = expenseRepository.getExpenseById(expenseId)
                ?: return Result.failure(IllegalArgumentException("Expense not found with ID: $expenseId"))

            updateExpensePayment(expenseId, expense.amount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFullyPaidExpenses(userId: String): Flow<List<Expense>> {
        return expenseRepository.getExpensesByUser(userId).map { expenses ->
            expenses.filter { PaymentValidation.isFullyPaid(it) }
        }
    }

    override suspend fun getPartiallyPaidExpenses(userId: String): Flow<List<Expense>> {
        return expenseRepository.getExpensesByUser(userId).map { expenses ->
            expenses.filter { PaymentValidation.isPartiallyPaid(it) }
        }
    }

    override suspend fun getUnpaidExpenses(userId: String): Flow<List<Expense>> {
        return expenseRepository.getExpensesByUser(userId).map { expenses ->
            expenses.filter { it.amountPaid == 0.0 }
        }
    }

    override suspend fun getPendingExpenses(userId: String): Flow<List<Expense>> {
        return expenseRepository.getExpensesByUser(userId).map { expenses ->
            expenses.filter { !PaymentValidation.isFullyPaid(it) }
        }
    }

    override suspend fun getTotalOutstandingExpense(userId: String): Flow<Double> {
        return expenseRepository.getExpensesByUser(userId).map { expenses ->
            expenses.sumOf { PaymentValidation.getOutstandingExpense(it) }
        }
    }

    override suspend fun getTotalPaidAmount(userId: String): Flow<Double> {
        return expenseRepository.getExpensesByUser(userId).map { expenses ->
            expenses.sumOf { it.amountPaid }
        }
    }
}
