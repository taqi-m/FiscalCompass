package com.fiscal.compass.domain.repository

import com.fiscal.compass.domain.model.base.Expense
import com.fiscal.compass.domain.model.ExpenseFull
import com.fiscal.compass.domain.model.ExpenseWithCategory
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    suspend fun addExpense(expense: Expense): Long

    suspend fun updateExpense(expense: Expense)

    suspend fun deleteExpense(expense: Expense)

    suspend fun deleteExpenseById(id: String)

    suspend fun getExpenseById(id: String): Expense?

    suspend fun getAllExpenses(): Flow<List<Expense>>

    suspend fun getExpensesByMonth(month: Int, year: Int): Flow<List<Expense>>

    suspend fun getExpensesByUser(userId: String): Flow<List<Expense>>


    suspend fun getExpensesWithCategory(userId: String): Flow<List<ExpenseWithCategory>>

    suspend fun getSingleFulExpenseById(id: String): ExpenseFull?

    suspend fun getAllFiltered(
        userIds: List<String>? = emptyList(),
        personIds: List<String>? = emptyList(),   // pass null to ignore
        categoryIds: List<String>? = emptyList(),  // pass null to ignore - changed from Long to String
        startDate: Long? = null,       // nullable → open start
        endDate: Long? = null          // nullable → open end
    ): Flow<List<Expense>>

    fun getSumByDateRange(userId: String? = null, startDate: Long, endDate: Long): Flow<Double>
    fun getNextExpenseId(): String
}