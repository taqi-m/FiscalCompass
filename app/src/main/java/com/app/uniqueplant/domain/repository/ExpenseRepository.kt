package com.app.uniqueplant.domain.repository

import com.app.uniqueplant.domain.model.Expense
import com.app.uniqueplant.domain.model.ExpenseFull
import com.app.uniqueplant.domain.model.ExpenseWithCategory
import com.app.uniqueplant.domain.model.Income
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    suspend fun addExpense(expense: Expense): Long

    suspend fun updateExpense(expense: Expense)

    suspend fun deleteExpense(expense: Expense)

    suspend fun deleteExpenseById(id: Long)

    suspend fun getExpenseById(id: Long): Expense?

    suspend fun getAllExpenses(): Flow<List<Expense>>

    suspend fun getExpensesByMonth(month: Int, year: Int): Flow<List<Expense>>

    suspend fun getExpensesByUser(userId: String): Flow<List<Expense>>

    suspend fun getExpensesByCategory(categoryId: Long): List<Expense>

    suspend fun getExpensesWithCategory(userId: String): Flow<List<ExpenseWithCategory>>

    suspend fun getSingleFulExpenseById(id: Long): ExpenseFull

    suspend fun getAllFiltered(
        userIds: List<String>? = emptyList() ,
        personIds: List<Long>? = emptyList(),   // pass null to ignore
        categoryIds: List<Long>? = emptyList(),  // pass null to ignore
        startDate: Long? = null,       // nullable → open start
        endDate: Long? = null          // nullable → open end
    ): Flow<List<Expense>>

    suspend fun getTotalExpenses(): Double

    suspend fun getExpensesByDateRange(startDate: String, endDate: String): List<Expense>
    fun getExpenseSumByMonth(month: Int, year: Int): Flow<Double>
}