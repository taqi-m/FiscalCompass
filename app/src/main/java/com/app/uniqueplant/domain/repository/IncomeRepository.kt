package com.app.uniqueplant.domain.repository

import com.app.uniqueplant.domain.model.Income
import com.app.uniqueplant.domain.model.IncomeFull
import com.app.uniqueplant.domain.model.IncomeWithCategory
import kotlinx.coroutines.flow.Flow

interface IncomeRepository {
    suspend fun addIncome(income: Income): Long

    suspend fun updateIncome(income: Income)

    suspend fun deleteIncome(income: Income)

    suspend fun deleteIncomeById(id: Long)

    suspend fun getIncomeById(id: Long): Income?

    suspend fun getAllIncomes(): Flow<List<Income>>

    suspend fun getIncomesByUser(userId: Long): Flow<List<Income>>

    suspend fun getIncomesByCategory(categoryId: Long): List<Income>

    suspend fun getIncomesWithCategory(userId: String): Flow<List<IncomeWithCategory>>

    suspend fun getSingleFullIncomeById(id: Long): IncomeFull

    suspend fun getAllFullIncomesFiltered(
        personIds: List<Long>?,   // pass null to ignore
        categoryIds: List<Long>?, // pass null to ignore
        startDate: Long?,       // nullable → open start
        endDate: Long?          // nullable → open end
    ): List<Income>

    suspend fun getTotalIncomes(): Double

    suspend fun getIncomeSumByMonth(month: Int, year: Int): Flow<Double>

    suspend fun getIncomesByDateRange(startDate: String, endDate: String): List<Income>
    suspend fun getIncomesByMonth(month: Int, year: Int): Flow<List<Income>>
}