package com.app.uniqueplant.domain.usecase.transaction

import com.app.uniqueplant.data.model.Expense
import com.app.uniqueplant.data.model.Income
import com.app.uniqueplant.domain.repository.ExpenseRepository
import com.app.uniqueplant.domain.repository.IncomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class LoadTransactionsUseCase @Inject constructor(
    private val incomeRepository: IncomeRepository,
    private val expenseRepository: ExpenseRepository
){
suspend fun loadAllTransactions(): Flow<List<Any>> {
    return combine(
        expenseRepository.getAllExpenses(),
        incomeRepository.getAllIncomes()
    ) { expenses, incomes ->
        val allTransactions = expenses + incomes
        allTransactions.sortedByDescending {
            when (it) {
                is Expense -> it.date
                is Income -> it.date
                else -> null
            }
        }
    }
}

    companion object{
        const val TAG = "LoadTransactionsUseCase"
    }
}