package com.fiscal.compass.domain.usecase.transaction

import com.fiscal.compass.data.rbac.Permission
import com.fiscal.compass.domain.repository.ExpenseRepository
import com.fiscal.compass.domain.repository.IncomeRepository
import com.fiscal.compass.domain.repository.UserRepository
import com.fiscal.compass.domain.usecase.rbac.CheckPermissionUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar
import javax.inject.Inject

class GetMonthlyBalanceUC @Inject constructor(
    private val userRepository: UserRepository,
    private val incomeRepository: IncomeRepository,
    private val expenseRepository: ExpenseRepository,
    private val checkPermission: CheckPermissionUseCase
){
    suspend operator fun invoke(month: Int, year: Int): Flow<Double> {
        var userId: String? = userRepository.getLoggedInUser()?.userId
            ?: throw IllegalStateException("User is not logged in")

        val canViewAll = checkPermission(Permission.VIEW_ALL_ANALYTICS)
        if (canViewAll){
            userId = null
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

        val expenseSum = expenseRepository.getSumByDateRange(userId, startDate, endDate)
        val incomeSum = incomeRepository.getSumByDateRange(userId, startDate, endDate)

        return combine(expenseSum, incomeSum) { expenses, incomes ->
            incomes - expenses
        }
    }
}