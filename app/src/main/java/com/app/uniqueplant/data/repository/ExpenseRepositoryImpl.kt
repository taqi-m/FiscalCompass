package com.app.uniqueplant.data.repository

import com.app.uniqueplant.data.datasource.local.dao.ExpenseDao
import com.app.uniqueplant.data.mapper.toDomain
import com.app.uniqueplant.data.mapper.toExpenseEntity
import com.app.uniqueplant.domain.model.Expense
import com.app.uniqueplant.domain.model.ExpenseWithCategory
import com.app.uniqueplant.domain.model.ExpenseWithCategoryAndPerson
import com.app.uniqueplant.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {

    override suspend fun addExpense(expense: Expense): Long {
        return expenseDao.insertExpense(expense.toExpenseEntity())
    }

    override suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense.toExpenseEntity())
    }

    override suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense.toExpenseEntity())
    }

    override suspend fun deleteExpenseById(id: Long) {
        val expense = expenseDao.getExpenseById(id)
            ?: throw IllegalArgumentException("Expense with id $id not found")
        expenseDao.deleteExpense(expense)
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
            }.time,
            endDate = Calendar.getInstance().apply {
                set(Calendar.MONTH, month)
                set(Calendar.YEAR, year)
                set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            }.time
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

    override suspend fun getExpensesWithCategoryAndPerson(id: Long): ExpenseWithCategoryAndPerson {
        return expenseDao.getExpensesWithCategoryAndPerson(id).toDomain()
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
            }.time,
            endDate = Calendar.getInstance().apply {
                set(Calendar.MONTH, month)
                set(Calendar.YEAR, year)
                set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            }.time
        )
    }
}