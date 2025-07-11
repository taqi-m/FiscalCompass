package com.app.uniqueplant.data.repository

import com.app.uniqueplant.data.datasource.local.dao.ExpenseDao
import com.app.uniqueplant.data.model.Expense
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {

    override suspend fun addExpense(expense: Expense): Long {
        return expenseDao.insertExpense(expense)
    }

    override suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense)
    }

    override suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }

    override suspend fun getExpenseById(id: Long): Expense? {
        return expenseDao.getExpenseById(id)
    }

    override suspend fun getAllExpenses(): List<Expense> {
        return expenseDao.getAllExpenses()
    }

    override suspend fun getExpensesByUser(userId: Long): Flow<List<Expense>> {
        return expenseDao.getAllExpensesByUser(userId)
    }

    override suspend fun getExpensesByCategory(categoryId: Long): List<Expense> {
        TODO("Not yet implemented")
    }

    override suspend fun getTotalExpenses(): Double {
        TODO("Not yet implemented")
    }

    override suspend fun getExpensesByDateRange(
        startDate: String,
        endDate: String
    ): List<Expense> {
        TODO("Not yet implemented")
    }
}