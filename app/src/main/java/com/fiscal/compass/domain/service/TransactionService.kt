package com.fiscal.compass.domain.service

import com.fiscal.compass.domain.model.Transaction
import com.fiscal.compass.domain.util.DateRange
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface TransactionService {
    suspend fun getTransactionById(transactionId: Long, isExpense: Boolean): Transaction

    suspend fun addTransaction(transaction: Transaction): Result<Long>

    suspend fun updateTransaction(transaction: Transaction): Result<Unit>

    suspend fun deleteTransaction(transactionId: Long, isExpense: Boolean)

    suspend fun searchTransactions(
        personIds: List<Long>? = null,
        categoryIds: List<Long>? = null,
        dateRange: DateRange? = null,
        filterType: String? = null
    ): Map<Date, List<Transaction>>

    suspend fun searchTransactions(
        personIds: List<Long>?,
        categoryIds: List<Long>?,
        startDate: Long?,
        endDate: Long?,
        filterType: String? = null
    ): Map<Date, List<Transaction>>

    suspend fun loadCurrentMonthTransactions(date: Date? = null): Flow<Map<Date, List<Transaction>>>

    suspend fun getCurrentMonthIncome(date: Date? = null): Flow<Double>

    suspend fun getCurrentMonthExpense(date: Date? = null): Flow<Double>

    suspend fun getMonthlyBalance(month: Int, year: Int): Flow<Double>

    suspend fun getCurrentMonthBalance(): Flow<Double>
}
