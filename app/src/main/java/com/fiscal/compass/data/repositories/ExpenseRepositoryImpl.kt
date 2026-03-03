package com.fiscal.compass.data.repositories

// Updated to use String IDs instead of Long IDs for expenses
import com.fiscal.compass.data.local.dao.ExpenseDao
import com.fiscal.compass.data.mappers.toDomain
import com.fiscal.compass.data.mappers.toEntity
import com.fiscal.compass.data.remote.RemoteUtil.ensureValidExpenseId
import com.fiscal.compass.data.remote.RemoteUtil.generateExpenseId
import com.fiscal.compass.domain.model.ExpenseFull
import com.fiscal.compass.domain.model.ExpenseWithCategory
import com.fiscal.compass.domain.model.base.Expense
import com.fiscal.compass.domain.model.sync.SyncType
import com.fiscal.compass.domain.repository.ExpenseRepository
import com.fiscal.compass.domain.sync.AutoSyncManager
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
        expenseDao.insert(newExpense)
        autoSyncManager.triggerSync(SyncType.EXPENSES)
        return 0L // Placeholder return since insert doesn't return ID for String-based IDs
    }

    override suspend fun updateExpense(expense: Expense) {
        // Get existing expense to preserve sync fields
        val existingExpense = expenseDao.getExpenseById(expense.expenseId)
            ?: throw IllegalArgumentException("Expense with id ${expense.expenseId} not found")
        
        // Convert domain model to entity and preserve critical fields
        val updatedExpense = expense.toEntity().copy(
            isDeleted = existingExpense.isDeleted, // Preserve deletion status
            createdAt = existingExpense.createdAt, // Preserve creation time
            updatedAt = System.currentTimeMillis(),
            needsSync = true,
            isSynced = false
        )
        
        expenseDao.update(updatedExpense)
        autoSyncManager.triggerSync(SyncType.EXPENSES)
    }

    override suspend fun deleteExpense(expense: Expense) {
        expenseDao.markExpenseAsDeleted(expense.expenseId, System.currentTimeMillis())
        autoSyncManager.triggerSync(SyncType.EXPENSES)
    }

    override suspend fun deleteExpenseById(id: String) {
        expenseDao.markExpenseAsDeleted(id, System.currentTimeMillis())
        autoSyncManager.triggerSync(SyncType.EXPENSES)
    }

    override suspend fun getExpenseById(id: String): Expense? {
        return expenseDao.getExpenseById(id)?.toDomain()
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


    override suspend fun getExpensesWithCategory(userId: String): Flow<List<ExpenseWithCategory>> {
        return expenseDao.getExpensesWithCategory(userId).map {
            it.map { expenseItem ->
                expenseItem.toDomain()
            }
        }
    }

    override suspend fun getSingleFulExpenseById(id: String): ExpenseFull? {
        return expenseDao.getSingleFullExpense(id)?.toDomain()
    }

    override suspend fun getAllFiltered(
        userIds: List<String>?,
        personIds: List<String>?,
        categoryIds: List<String>?,
        startDate: Long?,
        endDate: Long?
    ): Flow<List<Expense>> {
        val finalUserIds = userIds?.takeIf { it.isNotEmpty() } ?: emptyList()
        val finalPersonIds = personIds?.takeIf { it.isNotEmpty() } ?: emptyList()
        val finalCategoryIds = categoryIds?.takeIf { it.isNotEmpty() } ?: emptyList()
        
        return expenseDao.getAllFullExpensesFiltered(
            userIds = finalUserIds,
            hasUserIds = finalUserIds.isNotEmpty(),
            personIds = finalPersonIds,
            hasPersonIds = finalPersonIds.isNotEmpty(),
            categoryIds = finalCategoryIds,
            hasCategoryIds = finalCategoryIds.isNotEmpty(),
            startDate = startDate,
            endDate = endDate
        ).map {
            it.map { expenseEntity ->
                expenseEntity.toDomain()
            }
        }
    }


    override fun getSumByDateRange(
        userId: String?,
        startDate: Long,
        endDate: Long
    ): Flow<Double> {
        val userId = userId.takeIf { !it.isNullOrBlank() }
        return expenseDao.getExpenseSumByDateRange(userId, startDate, endDate)
    }

    override fun getNextExpenseId(): String {
        val newExpenseId = generateExpenseId()
        val validExpenseId = ensureValidExpenseId(newExpenseId)
        return validExpenseId
    }
}