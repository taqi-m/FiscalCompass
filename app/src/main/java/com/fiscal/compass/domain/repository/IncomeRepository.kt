package com.fiscal.compass.domain.repository

import com.fiscal.compass.domain.model.base.Income
import com.fiscal.compass.domain.model.IncomeFull
import com.fiscal.compass.domain.model.IncomeWithCategory
import kotlinx.coroutines.flow.Flow

interface IncomeRepository {
    suspend fun addIncome(income: Income): Long

    suspend fun updateIncome(income: Income)

    suspend fun deleteIncome(income: Income)

    suspend fun deleteIncomeById(id: String)

    suspend fun getAllIncomes(): Flow<List<Income>>

    suspend fun getIncomeById(id: String): Income?

    suspend fun getIncomesByUser(userId: String): Flow<List<Income>>


    suspend fun getIncomesWithCategory(userId: String): Flow<List<IncomeWithCategory>>

    suspend fun getSingleFullIncomeById(id: String): IncomeFull?

    suspend fun getAllFiltered(
        userIds: List<String>? = emptyList(),
        personIds: List<String>? = emptyList(),   // pass null to ignore
        categoryIds: List<String>? = emptyList(),  // pass null to ignore - changed from Long to String
        startDate: Long? = null,       // nullable → open start
        endDate: Long? = null          // nullable → open end
    ): Flow<List<Income>>

    suspend fun getSumByDateRange(userId:String? = null, startDate: Long, endDate: Long): Flow<Double>


    suspend fun getIncomesByMonth(month: Int, year: Int): Flow<List<Income>>
    fun getNextIncomeId(): String
}