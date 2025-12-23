package com.fiscal.compass.domain.service

import com.fiscal.compass.domain.model.base.Income
import com.fiscal.compass.domain.model.IncomeFull
import com.fiscal.compass.domain.model.IncomeWithCategory
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface IncomeService {
    suspend fun addIncome(
        amount: Double,
        categoryId: Long,
        description: String,
        date: Date,
        amountPaid: Double = 0.0
    ): Result<Unit>

    suspend fun getUserIncomes(userId: String): Flow<List<Income>>

    suspend fun getIncomesWithCategory(userId: String): Flow<List<IncomeWithCategory>>

    suspend fun getSingleFullIncomeById(id: Long): IncomeFull

    suspend fun updateIncomePayment(incomeId: Long, newAmountPaid: Double): Result<Unit>

    suspend fun addPayment(incomeId: Long, paymentAmount: Double): Result<Unit>

    suspend fun markAsFullyReceived(incomeId: Long): Result<Unit>

    suspend fun getFullyReceivedIncomes(userId: String): Flow<List<Income>>

    suspend fun getPartiallyReceivedIncomes(userId: String): Flow<List<Income>>

    suspend fun getUnpaidIncomes(userId: String): Flow<List<Income>>

    suspend fun getPendingIncomes(userId: String): Flow<List<Income>>

    suspend fun getTotalOutstandingIncome(userId: String): Flow<Double>

    suspend fun getTotalReceivedAmount(userId: String): Flow<Double>
}
