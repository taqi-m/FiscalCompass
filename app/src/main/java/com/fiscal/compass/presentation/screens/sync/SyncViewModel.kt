package com.fiscal.compass.presentation.screens.sync

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiscal.compass.domain.service.analytics.AnalyticsEvent
import com.fiscal.compass.domain.service.analytics.AnalyticsService
import com.fiscal.compass.domain.sync.AutoSyncManager
import com.fiscal.compass.domain.usecase.sync.ForceSyncUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val autoSyncManager: AutoSyncManager,
    private val forceSyncUseCase: ForceSyncUseCase,
    private val analyticsService: AnalyticsService,
) : ViewModel() {

    val state: StateFlow<SyncScreenState> = autoSyncManager.syncStatusFlow
        .map { status ->
            SyncScreenState(
                isSyncing       = status.isSyncing,
                isOnline        = status.isOnline,
                pendingExpenses = status.pendingExpenses,
                pendingIncomes  = status.pendingIncomes,
                lastSyncTime    = status.lastSyncTime,
                errorMessage    = status.syncError
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SyncScreenState()
        )

    fun onEvent(event: SyncEvent) {
        when (event) {
            is SyncEvent.ForceSync -> {
                val startTime = System.currentTimeMillis()
                analyticsService.logEvent(AnalyticsEvent.SyncStarted)
                viewModelScope.launch {
                    try {
                        forceSyncUseCase()
                        val duration = System.currentTimeMillis() - startTime
                        analyticsService.logEvent(AnalyticsEvent.SyncCompleted(0, duration))
                    } catch (e: Exception) {
                        Log.e(TAG, "Force sync failed", e)
                        analyticsService.logEvent(AnalyticsEvent.SyncFailed(e.message ?: "Unknown error"))
                    }
                }
            }
        }
    }

    private companion object {
        const val TAG = "SyncViewModel"
    }
}