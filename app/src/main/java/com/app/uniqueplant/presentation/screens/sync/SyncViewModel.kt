package com.app.uniqueplant.presentation.screens.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.uniqueplant.domain.interfaces.LocalDataSource
import com.app.uniqueplant.domain.interfaces.NetworkStateProvider
import com.app.uniqueplant.domain.interfaces.SyncService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val networkDataSource: NetworkStateProvider,
    private val syncService: SyncService
): ViewModel(){
    private val _state = MutableStateFlow(SyncScreenState())
    val state: StateFlow<SyncScreenState> = _state.asStateFlow()

    private val coroutineScope = viewModelScope
    private var unsyncedDataCollectionJob: Job? = null


    init {
        coroutineScope.launch {
            checkConnectivityStatus()
        }
        coroutineScope.launch {
            checkUnsyncedData()
            collectUnsyncedCounts()
        }
    }


    fun onEvent(event: SyncEvent) {
        when (event) {
            is SyncEvent.CancelSync -> {

            }
            is SyncEvent.SyncAll -> {
                coroutineScope.launch {
                    updateState {
                        copy(
                            isSyncing = true
                        )
                    }
                    try {
                        syncService.syncAllData()
                    } catch (e: Exception) {
                        // Handle exception
                    } finally {
                        updateState {
                            copy(
                                isSyncing = false
                            )
                        }
                        checkUnsyncedData()
                    }
                }
            }
        }
    }

    private suspend fun checkUnsyncedData() {
        val hasUnsyncedData = localDataSource.hasUnsyncedData()
        updateState {
            copy(
                hasUnsyncedData = hasUnsyncedData,
            )
        }
    }

    private fun collectUnsyncedCounts() {
        unsyncedDataCollectionJob?.cancel()
        unsyncedDataCollectionJob = coroutineScope.launch(Dispatchers.IO) {
            try {
                val dataFlow = combine(
                    localDataSource.getUnsyncedExpenseCount(),
                    localDataSource.getUnsyncedIncomeCount(),
                ) { expenseCount, incomeCount ->
                    Pair(expenseCount, incomeCount)
                }.collect { (expenseCount, incomeCount) ->
                    updateState {
                        copy(
                            unsyncedExpenseCount = expenseCount,
                            unsyncedIncomeCount = incomeCount,
                        )
                    }
                }
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }

    private suspend fun checkConnectivityStatus() {
        val isConnected = networkDataSource.networkStateFlow
        updateState {
            copy(
                isConnected = isConnected,
            )
        }
    }

    private fun resetState() {
        _state.value = SyncScreenState()
    }

    private fun updateState(update: SyncScreenState.() -> SyncScreenState) {
        _state.value = _state.value.update()
    }
}