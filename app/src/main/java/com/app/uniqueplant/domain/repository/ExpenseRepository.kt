package com.app.uniqueplant.domain.repository

import com.app.uniqueplant.data.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    suspend fun addExpense(expense: Expense): Long

    suspend fun updateExpense(expense: Expense)

    suspend fun deleteExpense(expense: Expense)

    suspend fun getExpenseById(id: Long): Expense?

    suspend fun getAllExpenses(): Flow<List<Expense>>

    suspend fun getExpensesByUser(userId: String): Flow<List<Expense>>

    suspend fun getExpensesByCategory(categoryId: Long): List<Expense>

    suspend fun getTotalExpenses(): Double

    suspend fun getExpensesByDateRange(startDate: String, endDate: String): List<Expense>
}