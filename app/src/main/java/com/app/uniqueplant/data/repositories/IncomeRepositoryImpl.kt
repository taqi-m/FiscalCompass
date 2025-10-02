package com.app.uniqueplant.data.repositories

import com.app.uniqueplant.data.managers.AutoSyncManager
import com.app.uniqueplant.data.managers.SyncType
import com.app.uniqueplant.data.mappers.toDomain
import com.app.uniqueplant.data.mappers.toIncomeEntity
import com.app.uniqueplant.data.mappers.toIncomeWithCategory
import com.app.uniqueplant.data.local.dao.IncomeDao
import com.app.uniqueplant.domain.model.Income
import com.app.uniqueplant.domain.model.IncomeFull
import com.app.uniqueplant.domain.model.IncomeWithCategory
import com.app.uniqueplant.domain.repository.IncomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

class IncomeRepositoryImpl @Inject constructor(
    private val incomeDao: IncomeDao,
    private val autoSyncManager: AutoSyncManager,
) : IncomeRepository {

    override suspend fun addIncome(income: Income): Long {
        val incomeEntity = income.toIncomeEntity()
        val dbResult = incomeDao.insert(incomeEntity)
        autoSyncManager.triggerSync(SyncType.INCOMES)
        return dbResult
    }

    override suspend fun updateIncome(income: Income) {
        incomeDao.update(income.toIncomeEntity())
    }

    override suspend fun deleteIncome(income: Income) {
        incomeDao.delete(income.toIncomeEntity())
    }

    override suspend fun deleteIncomeById(id: Long) {
        val income = incomeDao.getIncomeById(id)
            ?: throw IllegalArgumentException("Income with id $id not found")
        incomeDao.delete(income)
    }

    override suspend fun getIncomeById(id: Long): Income? {
        return incomeDao.getIncomeById(id)?.toDomain()
    }

    override suspend fun getAllIncomes(): Flow<List<Income>> {
        return incomeDao.getAllIncomes().map {
            it.map { incomeEntity ->
                incomeEntity.toDomain()
            }
        }
    }

    override suspend fun getIncomesByUser(userId: Long): Flow<List<Income>> {
        TODO("Not yet implemented")
    }

    override suspend fun getIncomesByCategory(categoryId: Long): List<Income> {
        TODO("Not yet implemented")
    }

    override suspend fun getIncomesWithCategory(userId: String): Flow<List<IncomeWithCategory>> {
        return incomeDao.getIncomesWithCategory(userId).map { incomeWithCategoryDboList ->
            incomeWithCategoryDboList.map { incomeWithCategoryDbo ->
                incomeWithCategoryDbo.toIncomeWithCategory()
            }
        }
    }

    override suspend fun getSingleFullIncomeById(id: Long): IncomeFull {
        return incomeDao.getSingleFullIncome(id).toDomain()
    }

    override suspend fun getAllFullIncomesFiltered(
        personIds: List<Long>?,
        categoryIds: List<Long>?,
        startDate: Long?,
        endDate: Long?
    ): List<Income> {
        val personIds = personIds?.takeIf { it.isNotEmpty() }
        val categoryIds = categoryIds?.takeIf { it.isNotEmpty() }
        return incomeDao.getAllFullIncomesFiltered(
            personIds = personIds,
            categoryIds = categoryIds,
            startDate = startDate,
            endDate = endDate
        ).map {
            it.toDomain()
        }
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
            }.time.time,
            endDate = Calendar.getInstance().apply {
                set(Calendar.MONTH, month)
                set(Calendar.YEAR, year)
                set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            }.time.time
        ).map {
            it.map { incomeEntity ->
                incomeEntity.toDomain()
            }
        }
    }

    override suspend fun getIncomeSumByMonth(month: Int, year: Int): Flow<Double> {
        return incomeDao.getIncomeSumByMonth(
            startDate = Calendar.getInstance().apply {
                set(Calendar.MONTH, month)
                set(Calendar.YEAR, year)
                set(Calendar.DAY_OF_MONTH, 1)
            }.time.time,
            endDate = Calendar.getInstance().apply {
                set(Calendar.MONTH, month)
                set(Calendar.YEAR, year)
                set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            }.time.time
        )
    }

    override suspend fun getIncomesByDateRange(
        startDate: String,
        endDate: String
    ): List<Income> {
        TODO("Not yet implemented")
    }
}