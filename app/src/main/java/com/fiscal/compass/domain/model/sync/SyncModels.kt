package com.fiscal.compass.domain.model.sync

enum class SyncType {
    EXPENSES, INCOMES, CATEGORIES, PERSONS,USERS, ALL
}

data class SyncStatus(
    val isOnline: Boolean = false,
    val isSyncing: Boolean = false,
    val pendingCategories: Int = 0,
    val pendingExpenses: Int = 0,
    val pendingIncomes: Int = 0,
    val lastSyncTime: Long? = null,
    val syncError: String? = null
)
data class SyncInfo(
    val lastFullSync: Long?,
    val lastExpenseSync: Long?,
    val lastIncomeSync: Long?
)