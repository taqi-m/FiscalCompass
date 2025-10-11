package com.app.uniqueplant.data.repositories

import com.app.uniqueplant.data.managers.AutoSyncManager
import com.app.uniqueplant.data.managers.SyncType
import com.app.uniqueplant.data.mappers.toDomain
import com.app.uniqueplant.data.mappers.toEntity
import com.app.uniqueplant.data.local.dao.ExpenseDao
import com.app.uniqueplant.domain.model.dataModels.Expense
import com.app.uniqueplant.domain.model.dataModels.ExpenseFull
import com.app.uniqueplant.domain.model.dataModels.ExpenseWithCategory
import com.app.uniqueplant.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val autoSyncManager: AutoSyncManager,
) : ExpenseRepository {

    override suspend fun addExpense(expense: Expense): Long {
        val newExpense = expense.toEntity()
        val dbResult = expenseDao.insert(newExpense)
        autoSyncManager.triggerSync(SyncType.EXPENSES)
        return dbResult
    }

    override suspend fun updateExpense(expense: Expense) {
        val updatedExpense = expense.toEntity().copy(
            updatedAt = System.currentTimeMillis(),
            needsSync = true
        )
        expenseDao.update(updatedExpense)
        autoSyncManager.triggerSync(SyncType.EXPENSES)
    }

    override suspend fun deleteExpense(expense: Expense) {
        expenseDao.markAsDeleted(expense.expenseId, System.currentTimeMillis())
//        expenseDao.delete(expense.toEntity())
        autoSyncManager.triggerSync(SyncType.EXPENSES)
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

    override suspend fun getAllFiltered(
        userIds: List<String>?,
        personIds: List<Long>?,
        categoryIds: List<Long>?,
        startDate: Long?,
        endDate: Long?
    ): Flow<List<Expense>> {
        val userIds = userIds?.takeIf { it.isNotEmpty() }
        val personIds = personIds?.takeIf { it.isNotEmpty() }
        val categoryIds = categoryIds?.takeIf { it.isNotEmpty() }
        return expenseDao.getAllFullExpensesFiltered(userIds, personIds, categoryIds, startDate, endDate).map {
            it.map { expenseEntity ->
                expenseEntity.toDomain()
            }
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

    override fun getSumByDateRange(
        userId: String?,
        startDate: Long,
        endDate: Long
    ): Flow<Double> {
        val userId = userId.takeIf { !it.isNullOrBlank() }
        return expenseDao.getExpenseSumByDateRange(userId, startDate, endDate)
    }
}