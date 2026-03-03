package com.fiscal.compass.presentation.screens.sync

data class SyncScreenState(
    val isSyncing: Boolean = false,
    val isOnline: Boolean = false,
    val pendingExpenses: Int = 0,
    val pendingIncomes: Int = 0,
    val lastSyncTime: Long? = null,
    val errorMessage: String? = null
)