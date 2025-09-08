package com.app.uniqueplant.data.manager

import android.util.Log
import com.app.uniqueplant.data.sources.local.AppDatabase
import com.app.uniqueplant.data.sources.preferences.PreferenceManager
import com.app.uniqueplant.data.sources.remote.sync.EnhancedSyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

enum class SyncType {
    EXPENSES, INCOMES, CATEGORIES, PERSONS, ALL
}

data class SyncStatus(
    val isOnline: Boolean = false,
    val isSyncing: Boolean = false,
    val pendingExpenses: Int = 0,
    val pendingIncomes: Int = 0,
    val lastSyncTime: Long? = null,
    val syncError: String? = null
)

@Singleton
class AutoSyncManager @Inject constructor(
    private val syncManager: EnhancedSyncManager,
    private val networkManager: NetworkManager,
    private val roomDatabase: AppDatabase,
//    private val coroutineScope: CoroutineScope,
    private val preferences: PreferenceManager
) {

    private lateinit var coroutineScope: CoroutineScope


    private val _syncStatusFlow = MutableStateFlow(SyncStatus())
    val syncStatusFlow: StateFlow<SyncStatus> = _syncStatusFlow.asStateFlow()

    private val syncChannel = Channel<SyncType>(Channel.UNLIMITED)
    private val syncQueue = mutableSetOf<SyncType>()
    private var isSyncing = false

    fun initialize(scope: CoroutineScope) {
        coroutineScope = scope
        observeNetworkChanges()
        observeUnsyncedData()
        startSyncProcessor()
    }

    fun triggerSync(syncType: SyncType) {
        Log.d(
            "AutoSyncManager",
            "Triggering sync for type: $syncType"
        )
        coroutineScope.launch {
            syncChannel.send(syncType)
            Log.d(
                "AutoSyncManager",
                "Sync request for $syncType sent to channel"
            )
        }
    }

    private fun startSyncProcessor() {
        coroutineScope.launch {
            for (syncType in syncChannel) {
                Log.d(
                    "AutoSyncManager",
                    "Received sync request for type: $syncType"
                )
                syncQueue.add(syncType)
                processSyncQueue()
            }
        }
    }

    private suspend fun processSyncQueue() {
        Log.d(
            "AutoSyncManager",
            "Processing sync queue: $syncQueue | isSyncing=$isSyncing | isOnline=${networkManager.isOnline()}"
        )
        if (syncQueue.isEmpty()) {
            Log.d("AutoSyncManager", "Sync queue is empty, returning")
            return
        }

        if (!networkManager.isOnline()) {
            Log.d("AutoSyncManager", "Network is offline, returning")
            return
        }

        if (isSyncing) {
            Log.d("AutoSyncManager", "Already syncing, returning")
            return
        }
        Log.d(
            "AutoSyncManager",
            "Starting sync process"
        )

        isSyncing = true
        updateSyncStatus { copy(isSyncing = true) }

        val typesToSync = syncQueue.toSet()

        try {
            // Process all queued sync types
            isSyncing = true
            updateSyncStatus { copy(isSyncing = true) }

            val typesToSync = syncQueue.toSet()
            syncQueue.clear()
            Log.d(
                "AutoSyncManager",
                "Starting sync for types: $typesToSync"
            )
            when {
                SyncType.ALL in typesToSync -> {
                    syncManager.syncAllData()
                }
                else -> {
                    typesToSync.forEach { type ->
                        when (type) {
                            SyncType.EXPENSES -> syncManager.syncExpenses()
                            SyncType.INCOMES -> syncManager.syncIncomes()
                            SyncType.CATEGORIES -> syncManager.syncCategories()
                            SyncType.PERSONS -> syncManager.syncPersons()
                            SyncType.ALL -> { /* Already handled above */ }
                        }
                    }
                }
            }

            updateSyncStatus {
                copy(
                    isSyncing = false,
                    lastSyncTime = System.currentTimeMillis(),
                    syncError = null
                )
            }

        } catch (e: Exception) {
            Log.e("AutoSync", "Sync failed", e)
            updateSyncStatus {
                copy(
                    isSyncing = false,
                    syncError = e.message
                )
            }

            // Retry logic with exponential backoff
            scheduleRetry(typesToSync)
        } finally {
            isSyncing = false
        }
    }

    private fun scheduleRetry(failedTypes: Set<SyncType>) {
        coroutineScope.launch {
            delay(getRetryDelay())
            failedTypes.forEach { type ->
                syncChannel.send(type)
            }
        }
    }

    private fun getRetryDelay(): Long {
        val retryCount = preferences.getInt("sync_retry_count", 0)
        val delay = minOf(1000L * (1 shl retryCount), 60000L) // Max 1 minute
        preferences.saveInt("sync_retry_count", retryCount + 1)
        return delay
    }

    private fun observeNetworkChanges() {
        coroutineScope.launch {
            networkManager.networkStateFlow.collect { isOnline ->
                updateSyncStatus { copy(isOnline = isOnline) }

                if (isOnline && hasUnsyncedData()) {
                    // Auto-sync when network becomes available
                    triggerSync(SyncType.ALL)
                }
            }
        }
    }

    private fun observeUnsyncedData() {
        coroutineScope.launch {
            // Monitor unsynced data counts
            combine(
                roomDatabase.expenseDao().getUnsyncedExpenseCount(),
                roomDatabase.incomeDao().getUnsyncedIncomeCount()
            ) { unsyncedExpenses, unsyncedIncomes ->
                updateSyncStatus {
                    copy(
                        pendingExpenses = unsyncedExpenses,
                        pendingIncomes = unsyncedIncomes
                    )
                }

                // Auto-trigger sync if there's unsynced data and we're online
                if ((unsyncedExpenses > 0 || unsyncedIncomes > 0) &&
                    networkManager.isOnline() && !isSyncing) {
                    delay(2000) // Small delay to batch operations
                    triggerSync(SyncType.ALL)
                }
            }.collect()
        }
    }

    private fun updateSyncStatus(update: SyncStatus.() -> SyncStatus) {
        _syncStatusFlow.value = _syncStatusFlow.value.update()
    }

    private suspend fun hasUnsyncedData(): Boolean {
        return roomDatabase.expenseDao().hasUnsyncedData() ||
                roomDatabase.incomeDao().hasUnsyncedData()
    }
}