package com.fiscal.compass.data.repositories

import com.fiscal.compass.data.managers.AutoSyncManager
import com.fiscal.compass.data.managers.SyncType
import com.fiscal.compass.data.mappers.toDomain
import com.fiscal.compass.data.mappers.toIncomeEntity
import com.fiscal.compass.data.mappers.toIncomeWithCategory
import com.fiscal.compass.data.local.dao.IncomeDao
import com.fiscal.compass.domain.model.base.Income
import com.fiscal.compass.domain.model.IncomeFull
import com.fiscal.compass.domain.model.IncomeWithCategory
import com.fiscal.compass.domain.repository.IncomeRepository
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
        val updatedIncome = income.toIncomeEntity().copy(
            updatedAt = System.currentTimeMillis(),
            needsSync = true
        )
        incomeDao.update(updatedIncome)
        autoSyncManager.triggerSync(SyncType.INCOMES)
    }

    override suspend fun deleteIncome(income: Income) {
        incomeDao.markIncomeAsDeleted(income.incomeId, System.currentTimeMillis())
        autoSyncManager.triggerSync(SyncType.INCOMES)
    }

    override suspend fun deleteIncomeById(id: Long) {
        val income = incomeDao.getById(id)
            ?: throw IllegalArgumentException("Income with id $id not found")
        incomeDao.delete(income)
    }

    override suspend fun getIncomeById(id: Long): Income? {
        return incomeDao.getById(id)?.toDomain()
    }

    override suspend fun getAllIncomes(): Flow<List<Income>> {
        return incomeDao.getAll().map {
            it.map { incomeEntity ->
                incomeEntity.toDomain()
            }
        }
    }

    override suspend fun getIncomesByUser(userId: String): Flow<List<Income>> {
        return incomeDao.getAllByUser(userId).map {
            it.map { incomeEntity ->
                incomeEntity.toDomain()
            }
        }
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

    override suspend fun getAllFiltered(userIds: List<String>? ,personIds: List<Long>?, categoryIds: List<Long>?, startDate: Long?, endDate: Long?): Flow<List<Income>> {
        val userIds = userIds?.takeIf { it.isNotEmpty() }
        val personIds = personIds?.takeIf { it.isNotEmpty() }
        val categoryIds = categoryIds?.takeIf { it.isNotEmpty() }
        return incomeDao.getAllFiltered(userIds, personIds, categoryIds, startDate, endDate).map {
            it.map { incomeEntity ->
                incomeEntity.toDomain()
            }
        }
    }

    override suspend fun getTotalIncomes(): Double {
        TODO("Not yet implemented")
    }

    override suspend fun getIncomesByMonth(month: Int, year: Int): Flow<List<Income>> {
        val calendar = Calendar.getInstance()
        val startDate = calendar.apply {
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
            set(Calendar.DAY_OF_MONTH, 1)
        }.time.time
        val endDate = calendar.apply {
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        }.time.time

        return incomeDao.getAllByDateRange(startDate, endDate).map {
            it.map { incomeEntity ->
                incomeEntity.toDomain()
            }
        }
    }

    override suspend fun getSumByDateRange(userId:String?, startDate: Long, endDate: Long): Flow<Double> {
        val userId = userId.takeIf { !it.isNullOrBlank() }
        return incomeDao.getSumByDateRange(userId, startDate, endDate)
    }

    override suspend fun getIncomesByDateRange(startDate: String, endDate: String): List<Income> {
        TODO("Not yet implemented")
    }

    override suspend fun getIncomesByDateRangeAndUser(startDate: String, endDate: String, userId: String): List<Income> {
        TODO("Not yet implemented")
    }
}