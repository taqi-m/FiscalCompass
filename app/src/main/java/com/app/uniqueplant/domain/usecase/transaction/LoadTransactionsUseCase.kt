package com.app.uniqueplant.domain.usecase.transaction

import com.app.uniqueplant.data.mappers.toTransaction
import com.app.uniqueplant.data.rbac.Permission
import com.app.uniqueplant.domain.model.Expense
import com.app.uniqueplant.domain.model.Income
import com.app.uniqueplant.domain.model.Transaction
import com.app.uniqueplant.domain.repository.AppPreferenceRepository
import com.app.uniqueplant.domain.repository.AuthRepository
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

class LoadTransactionsUseCase @Inject constructor(
    private val incomeRepository: IncomeRepository,
    private val expenseRepository: ExpenseRepository,
    private val userRepository: UserRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase
) {
    suspend fun loadAllTransactions(): Flow<List<Transaction>> {
        return combine(
            expenseRepository.getAllExpenses(),
            incomeRepository.getAllIncomes()
        ) { expenses, incomes ->
            val expenseTransactions = expenses.map { expense ->
                expense.toTransaction()
            }
            val incomeTransactions = incomes.map { income ->
                income.toTransaction()
            }
            expenseTransactions + incomeTransactions
        }
    }

    suspend fun loadCurrentMonthTransactions(): Flow<Map<Date, List<Transaction>>> {
        val userId = userRepository.getLoggedInUser()?.userId
        if (userId == null) {
            throw IllegalStateException("User is not logged in")
        }

        val userIds = mutableListOf(userId)
        val canViewAll = checkPermissionUseCase(Permission.VIEW_ALL_TRANSACTIONS)
        if (canViewAll){
            userIds.clear()
        }

        val calendar = Calendar.getInstance()
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

    suspend fun getCurrentMonthIncome(): Flow<Double> {
        return incomeRepository.getIncomeSumByMonth(
            month = Calendar.getInstance().get(Calendar.MONTH),
            year = Calendar.getInstance().get(Calendar.YEAR)
        )
    }

    suspend fun getCurrentMonthExpense(): Flow<Double> {
        return expenseRepository.getExpenseSumByMonth(
            month = Calendar.getInstance().get(Calendar.MONTH),
            year = Calendar.getInstance().get(Calendar.YEAR)
        )
    }

    suspend fun deleteTransaction(transactionId: Long, isExpense: Boolean) {
        when (isExpense) {
            true -> expenseRepository.deleteExpenseById(transactionId)
            false -> incomeRepository.deleteIncomeById(transactionId)
        }
    }
}