package com.fiscal.compass.presentation.screens.sync

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class SyncScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    // Add your state properties here
    val hasUnsyncedData: Boolean = false,
    val unsyncedExpenseCount: Int = 0,
    val unsyncedIncomeCount: Int = 0,
    val isConnected: StateFlow<Boolean> = MutableStateFlow(false),
    val isSyncing: Boolean = false
)