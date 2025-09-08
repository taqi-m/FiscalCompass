package com.app.uniqueplant.data.repository

import com.app.uniqueplant.data.manager.AutoSyncManager
import com.app.uniqueplant.data.manager.NetworkManager
import com.app.uniqueplant.data.manager.SyncType
import com.app.uniqueplant.data.mapper.toDomain
import com.app.uniqueplant.data.mapper.toEntity
import com.app.uniqueplant.data.sources.local.dao.CategoryDao
import com.app.uniqueplant.data.sources.local.dao.ExpenseDao
import com.app.uniqueplant.data.sources.remote.sync.EnhancedSyncManager
import com.app.uniqueplant.domain.model.Expense
import com.app.uniqueplant.domain.model.ExpenseFull
import com.app.uniqueplant.domain.model.ExpenseWithCategory
import com.app.uniqueplant.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao,
    private val syncManager: EnhancedSyncManager,
    private val networkManager: NetworkManager,
    private val autoSyncManager: AutoSyncManager,
//    private val coroutineScope: CoroutineScope
) : ExpenseRepository {

    override suspend fun addExpense(expense: Expense): Long {
        val expenseEntity = expense.toEntity()

        val dbResult = expenseDao.insert(expenseEntity)

        autoSyncManager.triggerSync(SyncType.EXPENSES)

        return dbResult
    }

    override suspend fun updateExpense(expense: Expense) {
        val expenseEntity = expense.toEntity()
        expenseDao.update(expenseEntity)
    }

    override suspend fun deleteExpense(expense: Expense) {
        expenseDao.delete(expense.toEntity())
    }

    override suspend fun deleteExpenseById(id: Long) {
        val expense = expenseDao.getExpenseById(id)
            ?: throw IllegalArgumentException("Expense with id $id not found")
        expenseDao.delete(expense)
    }

    override suspend fun getExpenseById(id: Long): Expense? {
        return expenseDao.getExpenseById(id)
            ?.toDomain()
            ?: throw IllegalArgumentException("Expense with id $id not found")
    }

    override suspend fun getAllExpenses(): Flow<List<Expense>> {
        return expenseDao.getAllExpenses().map {
            it.map { expenseEntity ->
                expenseEntity.toDomain()
            }
        }
    }

    override suspend fun getExpensesByMonth(month: Int, year: Int): Flow<List<Expense>> {
        return expenseDao.getExpensesByDateRangeForAllUsers(
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
            it.map { expenseEntity ->
                expenseEntity.toDomain()
            }
        }
    }

    override suspend fun getExpensesByUser(userId: String): Flow<List<Expense>> {
        return expenseDao.getAllExpensesByUser(userId).map {
            it.map { expenseEntity ->
                expenseEntity.toDomain()
            }
        }
    }

    override suspend fun getExpensesByCategory(categoryId: Long): List<Expense> {
        TODO("Not yet implemented")
    }

    override suspend fun getExpensesWithCategory(userId: String): Flow<List<ExpenseWithCategory>> {
        return expenseDao.getExpensesWithCategory(userId).map {
            it.map { expenseItem ->
                expenseItem.toDomain()
            }
        }
    }

    override suspend fun getSingleFulExpenseById(id: Long): ExpenseFull {
        return expenseDao.getSingleFullExpense(id).toDomain()
    }

    override suspend fun getAllFullExpensesFiltered(
        personIds: List<Long>?,
        categoryIds: List<Long>?,
        startDate: Long?,
        endDate: Long?
    ): List<Expense> {
        val personIds = personIds?.takeIf { it.isNotEmpty() }
        val categoryIds = categoryIds?.takeIf { it.isNotEmpty() }
        return expenseDao.getAllFullExpensesFiltered(
            personIds = personIds,
            categoryIds = categoryIds,
            startDate = startDate,
            endDate = endDate
        ).map {
            it.toDomain()
        }
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

    override fun getExpenseSumByMonth(
        month: Int,
        year: Int
    ): Flow<Double> {
        return expenseDao.getExpenseSumByMonth(
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
}