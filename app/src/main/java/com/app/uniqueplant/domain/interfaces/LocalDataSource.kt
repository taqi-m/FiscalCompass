package com.app.uniqueplant.domain.interfaces

import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun hasUnsyncedData(): Boolean
    fun getUnsyncedExpenseCount(): Flow<Int>
    fun getUnsyncedIncomeCount(): Flow<Int>
}