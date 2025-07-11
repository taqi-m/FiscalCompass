package com.app.uniqueplant.data.repository

import com.app.uniqueplant.data.datasource.local.dao.CategoryDao
import com.app.uniqueplant.data.datasource.local.dao.IncomeDao
import com.app.uniqueplant.data.model.Income
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IncomeRepositoryImpl @Inject constructor(
    private val incomeDao: IncomeDao,
    private val categoryDao: CategoryDao
) : IncomeRepository {

    override suspend fun addIncome(income: Income): Long {
        return incomeDao.insertIncome(income)
    }

    override suspend fun updateIncome(income: Income) {
        incomeDao.updateIncome(income)
    }

    override suspend fun deleteIncome(income: Income) {
        incomeDao.deleteIncome(income)
    }

    override suspend fun getIncomeById(id: Long): Income? {
        return incomeDao.getIncomeById(id)
    }

    override suspend fun getAllIncomes(): Flow<List<Income>> {
        return incomeDao.getAllIncomes()
    }

    override suspend fun getIncomesByUser(userId: Long): Flow<List<Income>> {
        TODO("Not yet implemented")
    }

    override suspend fun getIncomesByCategory(categoryId: Long): List<Income> {
        TODO("Not yet implemented")
    }

    override suspend fun getTotalIncomes(): Double {
        TODO("Not yet implemented")
    }

    override suspend fun getIncomesByDateRange(
        startDate: String,
        endDate: String
    ): List<Income> {
        TODO("Not yet implemented")
    }
}