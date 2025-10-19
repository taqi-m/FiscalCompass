package com.app.uniqueplant.data.managers

        import android.util.Log
        import com.app.uniqueplant.data.local.AppDatabase
        import com.app.uniqueplant.domain.repository.PreferenceManager
        import com.app.uniqueplant.data.remote.sync.EnhancedSyncManager
        import com.app.uniqueplant.domain.sync.SyncDependencyManager
        import com.google.firebase.auth.FirebaseAuth
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
            val pendingCategories: Int = 0,
            val pendingExpenses: Int = 0,
            val pendingIncomes: Int = 0,
            val lastSyncTime: Long? = null,
            val syncError: String? = null
        )

        @Singleton
        class AutoSyncManager @Inject constructor(
            private val syncManager: EnhancedSyncManager,
            private val timestampManager: SyncTimestampManager,
            private val dependencyManager: SyncDependencyManager,
            private val networkManager: NetworkManager,
            private val roomDatabase: AppDatabase,
            private val preferences: PreferenceManager,
            private val auth: FirebaseAuth
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
                Log.d(TAG, "Triggering sync for type: $syncType")
                coroutineScope.launch {
                    val userId = auth.currentUser?.uid ?: return@launch

                    // Check if sync is allowed based on dependencies
                    if (!dependencyManager.canSync(syncType, userId)) {
                        Log.w(TAG, "Cannot sync $syncType: dependencies not satisfied")
                        return@launch
                    }

                    updateSyncStatus { copy(isSyncing = true, syncError = null) }
                    syncChannel.send(syncType)
                    Log.d(TAG, "Sync request for $syncType sent to channel")
                }
            }

            private fun startSyncProcessor() {
                coroutineScope.launch {
                    for (syncType in syncChannel) {
                        Log.d(TAG, "Received sync request for type: $syncType")
                        syncQueue.add(syncType)
                        processSyncQueue()
                    }
                }
            }

            private suspend fun processSyncQueue() {
                val userId = auth.currentUser?.uid ?: return

                if (syncQueue.isEmpty()) {
                    return
                }

                if (!networkManager.isOnline()) {
                    updateSyncStatus { copy(isOnline = false, isSyncing = false, syncError = "No network connection") }
                    return
                }

                if (isSyncing) {
                    return
                }

                // Filter sync types based on dependencies
                val allowedSyncTypes = syncQueue.filter { syncType ->
                    dependencyManager.canSync(syncType, userId)
                }.toSet()

                if (allowedSyncTypes.isEmpty()) {
                    Log.w(TAG, "No sync types allowed due to dependency constraints")
                    syncQueue.clear()
                    updateSyncStatus { copy(isSyncing = false, syncError = "Dependency constraints not met") }
                    return
                }

                isSyncing = true
                updateSyncStatus { copy(isSyncing = true, syncError = null) }

                try {
                    // Update queue with only allowed types and clear the original queue
                    syncQueue.clear()
                    val typesToSync = allowedSyncTypes

                    Log.d(TAG, "Starting sync for types: $typesToSync")

                    // Sort by priority (critical first)
                    val sortedTypes = typesToSync.sortedBy { type ->
                        when (type) {
                            SyncType.CATEGORIES -> 0
                            SyncType.PERSONS -> 1
                            SyncType.EXPENSES -> 2
                            SyncType.INCOMES -> 3
                            SyncType.ALL -> 4
                        }
                    }

                    when {
                        SyncType.ALL in sortedTypes -> {
                            syncManager.syncAllData()
                            timestampManager.updateLastSyncTimestamp(SyncType.ALL)
                        }
                        else -> {
                            sortedTypes.forEach { type ->
                                when (type) {
                                    SyncType.CATEGORIES -> syncManager.syncCategories()
                                    SyncType.PERSONS -> syncManager.syncPersons()
                                    SyncType.EXPENSES -> syncManager.syncExpenses()
                                    SyncType.INCOMES -> syncManager.syncIncomes()
                                    SyncType.ALL -> { /* Already handled above */ }
                                }
                            }
                        }
                    }

                    preferences.saveInt("sync_retry_count", 0)

                    updateSyncStatus {
                        copy(
                            isSyncing = false,
                            lastSyncTime = System.currentTimeMillis(),
                            syncError = null
                        )
                    }

                } catch (e: Exception) {
                    Log.e(TAG, "Sync failed", e)
                    updateSyncStatus {
                        copy(
                            isSyncing = false,
                            syncError = e.message
                        )
                    }

                    // Retry logic with exponential backoff
                    scheduleRetry(allowedSyncTypes)
                } finally {
                    isSyncing = false
                    updateSyncStatus { copy(isSyncing = false) }
                }
            }

            private fun scheduleRetry(failedTypes: Set<SyncType>) {
                coroutineScope.launch {
                    delay(getRetryDelay())
                    failedTypes.forEach { type ->
                        triggerSync(type) // Use triggerSync to respect dependencies
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
                            // Auto-sync when network becomes available, but respect dependencies
                            val userId = auth.currentUser?.uid
                            if (userId != null && dependencyManager.isInitialized(SyncType.ALL, userId)) {
                                  //TODO: decide if we want to sync all or only unsynced types
//                                triggerSync(SyncType.ALL)
                            }
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
                        val userId = auth.currentUser?.uid
                        if ((unsyncedExpenses > 0 || unsyncedIncomes > 0) &&
                            networkManager.isOnline() && !isSyncing && userId != null &&
                            dependencyManager.isInitialized(SyncType.ALL, userId)) {
                            delay(2000) // Small delay to batch operations
                            //TODO: decide if we want to sync all or only unsynced types
//                            triggerSync(SyncType.ALL)
                        }
                    }.collect()
                }
            }

            fun getSyncInfo(): SyncInfo {
                return timestampManager.getLastSyncInfo()
            }

            private fun updateSyncStatus(update: SyncStatus.() -> SyncStatus) {
                _syncStatusFlow.value = _syncStatusFlow.value.update()
            }

            private suspend fun hasUnsyncedData(): Boolean {
                return roomDatabase.expenseDao().hasUnsyncedData() ||
                        roomDatabase.incomeDao().hasUnsyncedData()
            }

            private companion object {
                const val TAG = "AutoSyncManager"
            }
        }