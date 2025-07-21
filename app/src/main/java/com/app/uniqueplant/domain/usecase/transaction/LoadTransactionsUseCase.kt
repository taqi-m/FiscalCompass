package com.app.uniqueplant.domain.usecase.transaction

import com.app.uniqueplant.data.model.Expense
import com.app.uniqueplant.data.model.Income
import com.app.uniqueplant.domain.mapper.TransactionMapper
import com.app.uniqueplant.domain.model.Transaction
import com.app.uniqueplant.domain.repository.ExpenseRepository
import com.app.uniqueplant.domain.repository.IncomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar
import javax.inject.Inject

class LoadTransactionsUseCase @Inject constructor(
    private val incomeRepository: IncomeRepository,
    private val expenseRepository: ExpenseRepository
) {
    suspend fun loadAllTransactions(): Flow<List<Transaction>> {
        return combine(
            expenseRepository.getAllExpenses(),
            incomeRepository.getAllIncomes()
        ) { expenses, incomes ->
            val expenseTransactions = expenses.map { expense ->
                TransactionMapper.mapExpenseToTransaction(expense)
            }
            val incomeTransactions = incomes.map { income ->
                TransactionMapper.mapIncomeToTransaction(income)
            }
            expenseTransactions + incomeTransactions
        }
    }

    suspend fun loadCurrentMonthTransactions(): Flow<List<Transaction>> {
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
            val expenseTransactions = expenses.map { expense ->
                TransactionMapper.mapExpenseToTransaction(expense)
            }
            val incomeTransactions = incomes.map { income ->
                TransactionMapper.mapIncomeToTransaction(income)
            }
            expenseTransactions + incomeTransactions
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

    suspend fun deleteTransaction(transaction: Any?) {
        when (transaction) {
            is Expense -> expenseRepository.deleteExpense(transaction)
            is Income -> incomeRepository.deleteIncome(transaction)
            else -> throw IllegalArgumentException("Unknown transaction type")
        }
    }
}