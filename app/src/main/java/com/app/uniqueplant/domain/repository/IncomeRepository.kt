package com.app.uniqueplant.domain.repository

import com.app.uniqueplant.domain.model.dataModels.Income
import com.app.uniqueplant.domain.model.dataModels.IncomeFull
import com.app.uniqueplant.domain.model.dataModels.IncomeWithCategory
import kotlinx.coroutines.flow.Flow

interface IncomeRepository {
    suspend fun addIncome(income: Income): Long

    suspend fun updateIncome(income: Income)

    suspend fun deleteIncome(income: Income)

    suspend fun deleteIncomeById(id: Long)

    suspend fun getAllIncomes(): Flow<List<Income>>

    suspend fun getIncomeById(id: Long): Income?

    suspend fun getIncomesByUser(userId: String): Flow<List<Income>>

    suspend fun getIncomesByCategory(categoryId: Long): List<Income>

    suspend fun getIncomesWithCategory(userId: String): Flow<List<IncomeWithCategory>>

    suspend fun getSingleFullIncomeById(id: Long): IncomeFull

    suspend fun getAllFiltered(
        userIds: List<String>? = emptyList() ,
        personIds: List<Long>? = emptyList(),   // pass null to ignore
        categoryIds: List<Long>? = emptyList(),  // pass null to ignore
        startDate: Long? = null,       // nullable → open start
        endDate: Long? = null          // nullable → open end
    ): Flow<List<Income>>

    suspend fun getTotalIncomes(): Double

    suspend fun getSumByDateRange(userId:String? = null, startDate: Long, endDate: Long): Flow<Double>

    suspend fun getIncomesByDateRange(startDate: String, endDate: String): List<Income>
    suspend fun getIncomesByDateRangeAndUser(startDate: String, endDate: String, userId: String): List<Income>

    suspend fun getIncomesByMonth(month: Int, year: Int): Flow<List<Income>>
}