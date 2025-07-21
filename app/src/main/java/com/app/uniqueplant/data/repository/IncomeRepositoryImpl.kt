package com.app.uniqueplant.data.repository

import com.app.uniqueplant.data.datasource.local.dao.IncomeDao
import com.app.uniqueplant.data.model.Income
import com.app.uniqueplant.domain.repository.IncomeRepository
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject

class IncomeRepositoryImpl @Inject constructor(
    private val incomeDao: IncomeDao
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

    override suspend fun getIncomesByMonth(month: Int, year: Int): Flow<List<Income>> {
        return incomeDao.getIncomesByDateRangeForAllUsers(
            startDate = Calendar.getInstance().apply {
                set(Calendar.MONTH, month)
                set(Calendar.YEAR, year)
                set(Calendar.DAY_OF_MONTH, 1)
            }.time,
            endDate = Calendar.getInstance().apply {
                set(Calendar.MONTH, month)
                set(Calendar.YEAR, year)
                set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            }.time
        )
    }

    override suspend fun getIncomeSumByMonth(month: Int, year: Int): Flow<Double> {
        return incomeDao.getIncomeSumByMonth(
            startDate = Calendar.getInstance().apply {
                set(Calendar.MONTH, month)
                set(Calendar.YEAR, year)
                set(Calendar.DAY_OF_MONTH, 1)
            }.time,
            endDate = Calendar.getInstance().apply {
                set(Calendar.MONTH, month)
                set(Calendar.YEAR, year)
                set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            }.time
        )
    }

    override suspend fun getIncomesByDateRange(
        startDate: String,
        endDate: String
    ): List<Income> {
        TODO("Not yet implemented")
    }
}