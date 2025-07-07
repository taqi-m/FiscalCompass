package com.app.uniqueplant.data.datasource.local.repository

import com.app.uniqueplant.data.model.ExpenseWithCategory
import com.app.uniqueplant.data.datasource.local.dao.ExpenseDao
import com.app.uniqueplant.data.model.Expense
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao
) {
    suspend fun insertExpense(expense: Expense): Long {
        return expenseDao.insertExpense(expense)
    }
    
    suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense)
    }
    
    suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }
    
    fun getAllExpensesByUser(userId: Long): Flow<List<Expense>> {
        return expenseDao.getAllExpensesByUser(userId)
    }
    
    suspend fun getExpenseById(id: Long): Expense? {
        return expenseDao.getExpenseById(id)
    }
    
    fun getExpensesWithCategory(userId: Long): Flow<List<ExpenseWithCategory>> {
        return expenseDao.getExpensesWithCategory(userId)
    }
    
    fun getExpensesByDateRange(userId: Long, startDate: Date, endDate: Date): Flow<List<Expense>> {
        return expenseDao.getExpensesByDateRange(userId, startDate, endDate)
    }
    
    fun getExpensesByCategory(userId: Long, categoryId: Long): Flow<List<Expense>> {
        return expenseDao.getExpensesByCategory(userId, categoryId)
    }
    
    fun getTotalExpensesByUser(userId: Long): Flow<Double?> {
        return expenseDao.getTotalExpensesByUser(userId)
    }
    
    fun getExpenseSumByDateRange(userId: Long, startDate: Date, endDate: Date): Flow<Double?> {
        return expenseDao.getExpenseSumByDateRange(userId, startDate, endDate)
    }
    
    fun getExpenseSumByCategory(userId: Long): Flow<Map<Long?, Double>> {
        return expenseDao.getExpenseSumByCategory(userId)
    }
    
    suspend fun getExpenseCount(userId: Long): Int {
        return expenseDao.getExpenseCount(userId)
    }
}