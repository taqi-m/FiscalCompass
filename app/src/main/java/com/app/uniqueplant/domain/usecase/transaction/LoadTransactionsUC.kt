package com.app.uniqueplant.domain.usecase.transaction

import com.app.uniqueplant.data.mappers.toTransaction
import com.app.uniqueplant.data.rbac.Permission
import com.app.uniqueplant.domain.model.dataModels.Expense
import com.app.uniqueplant.domain.model.dataModels.Income
import com.app.uniqueplant.domain.model.dataModels.Transaction
import com.app.uniqueplant.domain.repository.ExpenseRepository
import com.app.uniqueplant.domain.repository.IncomeRepository
import com.app.uniqueplant.domain.repository.UserRepository
import com.app.uniqueplant.domain.usecase.rbac.CheckPermissionUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class LoadTransactionsUC @Inject constructor(
    private val incomeRepository: IncomeRepository,
    private val expenseRepository: ExpenseRepository,
    private val userRepository: UserRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase
) {

    private suspend fun getCurrentUserId(): String {
        val userId = userRepository.getLoggedInUser()?.userId
        if (userId != null ) {
            if (checkPermissionUseCase(Permission.VIEW_ALL_TRANSACTIONS))
            return ""
            return userId
        }
        return ""
    }

    suspend fun currentMonthTransactions(date: Date? = null): Flow<Map<Date, List<Transaction>>> {
        val userId = getCurrentUserId()
        val userIds = mutableListOf(userId)
        val canViewAll = checkPermissionUseCase(Permission.VIEW_ALL_TRANSACTIONS)
        if (canViewAll){
            userIds.clear()
        }

        val calendar = Calendar.getInstance()
        date?.let { calendar.time = it }
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
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


        val expenseFlow = expenseRepository.getAllFiltered(userIds = userIds,startDate = startDate, endDate = endDate)
        val incomeFlow = incomeRepository.getAllFiltered(userIds = userIds,startDate = startDate, endDate = endDate)

        return combine(expenseFlow, incomeFlow) { expenses, incomes ->
            mergeAndGroupTransactions(expenses,incomes)
        }
    }

    private fun mergeAndGroupTransactions(expenses: List<Expense>, incomes: List<Income>): Map<Date, List<Transaction>> {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val expenseTransactions = expenses.map { expense ->
            expense.toTransaction()
        }
        val incomeTransactions = incomes.map { income ->
            income.toTransaction()
        }
        return (expenseTransactions + incomeTransactions)
            .sortedByDescending { it.date }
            .groupBy { transaction ->
                @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                dateFormatter.parse(dateFormatter.format(transaction.date))
            }
    }

    suspend fun getCurrentMonthIncome(date: Date? = null): Flow<Double> {
        val calendar = Calendar.getInstance()
        date?.let { calendar.time = it }
        val startDate = calendar.apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time.time
        val endDate = calendar.apply {
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.time.time
        return incomeRepository.getSumByDateRange(
            userId = getCurrentUserId(),
            startDate = startDate,
            endDate = endDate
        )
    }

    suspend fun getCurrentMonthExpense(date: Date? = null): Flow<Double> {
        val calendar = Calendar.getInstance()
        date?.let { calendar.time = it }
        val startDate = calendar.apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time.time
        val endDate = calendar.apply {
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.time.time
        return expenseRepository.getSumByDateRange(
            userId = getCurrentUserId(),
            startDate = startDate,
            endDate = endDate
        )
    }

    suspend fun deleteTransaction(transactionId: Long, isExpense: Boolean) {
        when (isExpense) {
            true -> expenseRepository.deleteExpenseById(transactionId)
            false -> incomeRepository.deleteIncomeById(transactionId)
        }
    }
}