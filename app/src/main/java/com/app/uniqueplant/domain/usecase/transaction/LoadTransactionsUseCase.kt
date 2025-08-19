package com.app.uniqueplant.domain.usecase.transaction

import com.app.uniqueplant.data.mapper.toTransaction
import com.app.uniqueplant.domain.model.Transaction
import com.app.uniqueplant.domain.repository.ExpenseRepository
import com.app.uniqueplant.domain.repository.IncomeRepository
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
        return combine(
            expenseRepository.getExpensesByMonth(
                month = Calendar.getInstance().get(Calendar.MONTH),
                year = Calendar.getInstance().get(Calendar.YEAR)
            ),
            incomeRepository.getIncomesByMonth(
                month = Calendar.getInstance().get(Calendar.MONTH),
                year = Calendar.getInstance().get(Calendar.YEAR)
            )
        ) { expenses, incomes ->
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val expenseTransactions = expenses.map { expense ->
                expense.toTransaction()
            }
            val incomeTransactions = incomes.map { income ->
                income.toTransaction()
            }
            (expenseTransactions + incomeTransactions)
                .sortedByDescending { it.date }
                .groupBy { transaction ->
                    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                    dateFormatter.parse(dateFormatter.format(transaction.date))
                }
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