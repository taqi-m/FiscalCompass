package com.fiscal.compass.domain.interfaces

import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun hasUnsyncedData(): Boolean
    fun getUnsyncedExpenseCount(): Flow<Int>
    fun getUnsyncedIncomeCount(): Flow<Int>
}