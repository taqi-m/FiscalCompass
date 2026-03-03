package com.fiscal.compass.domain.interfaces

import kotlinx.coroutines.flow.Flow


interface SyncService {
    suspend fun syncAllData(): Flow<Int>
    suspend fun syncCategories()
    suspend fun syncPersons()
    suspend fun syncExpenses()
    suspend fun syncIncomes()
}