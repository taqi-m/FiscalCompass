package com.app.uniqueplant.domain.usecase.analytics

import com.app.uniqueplant.domain.model.MonthlyReport
import com.app.uniqueplant.domain.repository.CategoryRepository
import com.app.uniqueplant.domain.repository.ExpenseRepository
import com.app.uniqueplant.domain.repository.IncomeRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

// Placeholder for GetMonthlyReportUseCase


class GetMonthlyReportUseCase(
    private val incomesRepository: IncomeRepository,
    private val expensesRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(month: Int, year: Int): MonthlyReport {

        return combine(
            incomesRepository.getIncomesByMonth(month, year),
            expensesRepository.getExpensesByMonth(month, year)
        ) { incomesFlow, expensesFlow ->

            val incomes = incomesFlow.groupBy(
                keySelector = { income ->
                    categoryRepository.getCategoryNameById(income.categoryId) ?: ""
                },
                valueTransform = { income -> income.amount }
            ).mapValues { entry -> entry.value.sum() }

            val expenses = expensesFlow.groupBy(
                keySelector = { expense ->
                    categoryRepository.getCategoryNameById(expense.categoryId) ?: ""
                },
                valueTransform = { expense -> expense.amount }
            ).mapValues { entry -> entry.value.sum() }

            val totalIncomes = incomes.values.sum()
            val totalExpenses = expenses.values.sum()
            val totalProfit = totalIncomes - totalExpenses

            MonthlyReport(
                incomes = incomes,
                expenses = expenses,
                totalIncomes = totalIncomes,
                totalExpenses = totalExpenses,
                totalProfit = totalProfit
            )
        }.first()

    }
}