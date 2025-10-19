package com.app.uniqueplant.domain.interfaces


interface SyncService {
    suspend fun syncAllData()
    suspend fun syncCategories()
    suspend fun syncPersons()
    suspend fun syncExpenses()
    suspend fun syncIncomes()
}