package com.fiscal.compass.domain.service

import com.fiscal.compass.domain.model.base.Expense
import com.fiscal.compass.domain.model.ExpenseFull
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface ExpenseService {
    suspend fun addExpense(
        amount: Double,
        categoryId: Long,
        description: String,
        date: Date,
        amountPaid: Double = 0.0
    ): Result<Unit>

    suspend fun getUserExpenses(userId: String): Flow<List<Expense>>

    suspend fun getExpenseWithCategoryAndPerson(id: Long): ExpenseFull

    suspend fun updateExpensePayment(expenseId: Long, newAmountPaid: Double): Result<Unit>

    suspend fun addPayment(expenseId: Long, paymentAmount: Double): Result<Unit>

    suspend fun markAsFullyPaid(expenseId: Long): Result<Unit>

    suspend fun getFullyPaidExpenses(userId: String): Flow<List<Expense>>

    suspend fun getPartiallyPaidExpenses(userId: String): Flow<List<Expense>>

    suspend fun getUnpaidExpenses(userId: String): Flow<List<Expense>>

    suspend fun getPendingExpenses(userId: String): Flow<List<Expense>>

    suspend fun getTotalOutstandingExpense(userId: String): Flow<Double>

    suspend fun getTotalPaidAmount(userId: String): Flow<Double>
}
