package com.app.uniqueplant.domain.repository

interface TransactionRepository {
    suspend fun loadTransactions(): List<Any>

    suspend fun addTransaction(transaction: Any): Long

    suspend fun getTransactionsByCategoryId(categoryId: Long): List<Any>

    suspend fun getTransactionsByDateRange(startDate: Long, endDate: Long): List<Any>
}