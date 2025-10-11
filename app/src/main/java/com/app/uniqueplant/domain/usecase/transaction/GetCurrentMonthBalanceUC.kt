package com.app.uniqueplant.domain.usecase.transaction

import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject

class GetCurrentMonthBalanceUC @Inject constructor(
    private val getMonthlyBalanceUC: GetMonthlyBalanceUC
) {
    suspend operator fun invoke(): Flow<Double> {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)
        return getMonthlyBalanceUC(currentMonth, currentYear)
    }
}