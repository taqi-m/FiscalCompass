package com.app.uniqueplant.data.remote.sync

import android.util.Log
import com.app.uniqueplant.domain.sync.SyncDependencyManager
import com.app.uniqueplant.data.managers.SyncTimestampManager
import com.app.uniqueplant.data.managers.SyncType
import com.app.uniqueplant.data.rbac.Permission
import com.app.uniqueplant.domain.usecase.rbac.CheckPermissionUseCase
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class EnhancedSyncManager @Inject constructor(
    private val expenseSyncManager: ExpenseSyncManager,
    private val incomeSyncManager: IncomeSyncManager,
    private val categorySyncManager: CategorySyncManager,
    private val personSyncManager: PersonSyncManager,
    private val auth: FirebaseAuth,
    private val timestampManager: SyncTimestampManager,
    private val dependencyManager: SyncDependencyManager,
    private val checkPermissionUseCase: CheckPermissionUseCase
) {

    suspend fun syncAllData() {
        syncCategories()
        syncExpenses()
        syncIncomes()
    }

    suspend fun syncExpenses() {
        val userId = auth.currentUser?.uid ?: return

        // Check dependencies before syncing (if you have access to dependencyManager)
        if (!dependencyManager.canSync(SyncType.EXPENSES, userId)) {
            Log.w(TAG, "Cannot sync expenses: dependencies not satisfied")
            return
        }

        try {
            // 1. Upload new/modified local expenses
            expenseSyncManager.uploadLocalExpenses(userId)

            // 2. Download remote expenses
            expenseSyncManager.downloadRemoteExpenses(userId)

            //3. Update last sync timestamp
            timestampManager.updateLastSyncTimestamp(SyncType.EXPENSES)

        } catch (e: Exception) {
            Log.e(TAG, "Error syncing data", e)
        }
    }

    suspend fun syncIncomes() {
        val userId = auth.currentUser?.uid ?: return
        try {
            // 1. Upload new/modified local incomes
            Log.d(TAG, "Starting income sync for user $userId")
            incomeSyncManager.uploadLocalIncomes(userId)
            // 2. Download remote incomes
            incomeSyncManager.downloadRemoteIncomes(userId)

            //3. Update last sync timestamp
            timestampManager.updateLastSyncTimestamp(SyncType.INCOMES)

        } catch (e: Exception) {
            Log.e(TAG, "Error syncing data", e)
        }
    }

    suspend fun syncPersons() {
        if (checkPermissionUseCase(Permission.ADD_PERSON)) {
            personSyncManager.uploadLocalPersons()
        }
        personSyncManager.downloadRemotePersons()
    }

    suspend fun syncCategories() {
        if (checkPermissionUseCase(Permission.ADD_CATEGORY)) {
            categorySyncManager.uploadLocalCategories()
        }
        categorySyncManager.downloadRemoteCategories()
    }

    suspend fun initializeCategories(userId: String) {
        Log.d(TAG, "Initializing categories")

        try {
            categorySyncManager.downloadRemoteCategories()

            Log.d(TAG, "Categories initialization completed")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize categories", e)
            throw e
        }
    }

    suspend fun initializePersons(userId: String) {
        Log.d(TAG, "Initializing persons")

        try {
            personSyncManager.downloadRemotePersons()
            Log.d(TAG, "Persons initialization completed")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize persons", e)
            throw e
        }
    }

    suspend fun initializeExpenses(userId: String) {
        Log.d(TAG, "Initializing expenses for user: $userId")

        try {
            // Use existing download method but with initialization flag
            expenseSyncManager.downloadRemoteExpenses(userId, isInitialization = true)

            Log.d(TAG, "Expenses initialization completed")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize expenses", e)
            throw e
        }
    }

    suspend fun initializeIncomes(userId: String) {
        Log.d(TAG, "Initializing incomes for user: $userId")

        try {
            // Use existing download method but with initialization flag
            incomeSyncManager.downloadRemoteIncomes(userId, isInitialization = true)

            Log.d(TAG, "Incomes initialization completed")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize incomes", e)
            throw e
        }
    }

    companion object {
        const val TAG = "SyncManager"
    }
}