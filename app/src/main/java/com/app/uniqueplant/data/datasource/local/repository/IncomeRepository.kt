package com.app.uniqueplant.data.datasource.local.repository

import com.app.uniqueplant.data.model.IncomeWithCategory
import com.app.uniqueplant.data.datasource.local.dao.IncomeDao
import com.app.uniqueplant.data.model.Income
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IncomeRepository @Inject constructor(
    private val incomeDao: IncomeDao
) {
    suspend fun insertIncome(income: Income): Long {
        return incomeDao.insertIncome(income)
    }
    
    suspend fun updateIncome(income: Income) {
        incomeDao.updateIncome(income)
    }
    
    suspend fun deleteIncome(income: Income) {
        incomeDao.deleteIncome(income)
    }
    
    fun getAllIncomesByUser(userId: Long): Flow<List<Income>> {
        return incomeDao.getAllIncomesByUser(userId)
    }
    
    suspend fun getIncomeById(id: Long): Income? {
        return incomeDao.getIncomeById(id)
    }
    
    fun getIncomesWithCategory(userId: Long): Flow<List<IncomeWithCategory>> {
        return incomeDao.getIncomesWithCategory(userId)
    }
    
    fun getIncomesByDateRange(userId: Long, startDate: Date, endDate: Date): Flow<List<Income>> {
        return incomeDao.getIncomesByDateRange(userId, startDate, endDate)
    }
    
    fun getIncomesByCategory(userId: Long, categoryId: Long): Flow<List<Income>> {
        return incomeDao.getIncomesByCategory(userId, categoryId)
    }
    
    fun getTotalIncomeByUser(userId: Long): Flow<Double?> {
        return incomeDao.getTotalIncomeByUser(userId)
    }
    
    fun getIncomeSumByDateRange(userId: Long, startDate: Date, endDate: Date): Flow<Double?> {
        return incomeDao.getIncomeSumByDateRange(userId, startDate, endDate)
    }
    
    fun getIncomeSumByCategory(userId: Long): Flow<Map<Long?, Double>> {
        return incomeDao.getIncomeSumByCategory(userId)
    }
    
    suspend fun getIncomeCount(userId: Long): Int {
        return incomeDao.getIncomeCount(userId)
    }
}