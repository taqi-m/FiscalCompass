package com.app.uniqueplant.domain.usecase.analytics

import com.app.uniqueplant.domain.model.MonthlyReport
import com.app.uniqueplant.domain.repository.CategoryRepository
import com.app.uniqueplant.domain.repository.ExpenseRepository
import com.app.uniqueplant.domain.repository.IncomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetMonthlyReportUseCase @Inject constructor(
    private val incomesRepository: IncomeRepository,
    private val expensesRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(month: Int, year: Int): Flow<MonthlyReport> {
        return combine(
            incomesRepository.getIncomesByMonth(month, year),
            expensesRepository.getExpensesByMonth(month, year)
        ) { incomes, expenses ->

            val incomesByCategory = incomes.groupBy(
                keySelector = { income ->
                    categoryRepository.getCategoryNameById(income.categoryId) ?: ""
                },
                valueTransform = { income -> income.amount }
            ).mapValues { entry -> entry.value.sum() }

            val expensesByCategory = expenses.groupBy(
                keySelector = { expense ->
                    categoryRepository.getCategoryNameById(expense.categoryId) ?: ""
                },
                valueTransform = { expense -> expense.amount }
            ).mapValues { entry -> entry.value.sum() }

            val totalIncomes = incomesByCategory.values.sum()
            val totalExpenses = expensesByCategory.values.sum()
            val totalProfit = totalIncomes - totalExpenses

            MonthlyReport(
                incomes = incomesByCategory,
                expenses = expensesByCategory,
                totalIncomes = totalIncomes,
                totalExpenses = totalExpenses,
                totalProfit = totalProfit
            )
        }
    }
}