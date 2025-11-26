package com.fiscal.compass.domain.usecase.analytics

import com.fiscal.compass.data.rbac.Permission
import com.fiscal.compass.domain.model.MonthlyReport
import com.fiscal.compass.domain.repository.CategoryRepository
import com.fiscal.compass.domain.repository.ExpenseRepository
import com.fiscal.compass.domain.repository.IncomeRepository
import com.fiscal.compass.domain.repository.UserRepository
import com.fiscal.compass.domain.usecase.rbac.CheckPermissionUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar
import javax.inject.Inject

class GetMonthlyReportUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val checkPermission: CheckPermissionUseCase,
    private val incomesRepository: IncomeRepository,
    private val expensesRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(month: Int, year: Int): Flow<MonthlyReport> {
        val userId = userRepository.getLoggedInUser()?.userId
        if (userId == null) {
            throw IllegalStateException("User is not logged in")
        }

        val userIds = mutableListOf(userId)
        val canViewAll = checkPermission(Permission.VIEW_ALL_ANALYTICS)
        if (canViewAll){
            userIds.clear()
        }

        val calendar = Calendar.getInstance()
        val startDate = calendar.apply {
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
        }.time.time
        val endDate = calendar.apply {
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        }.time.time


        val expenseFlow = expensesRepository.getAllFiltered(userIds = userIds, startDate = startDate, endDate = endDate)
        val incomeFlow = incomesRepository.getAllFiltered(userIds = userIds, startDate = startDate, endDate = endDate)

        return combine(
            incomeFlow,
            expenseFlow
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