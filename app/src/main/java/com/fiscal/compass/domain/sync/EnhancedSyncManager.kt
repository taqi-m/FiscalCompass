package com.fiscal.compass.domain.sync

import android.util.Log
import com.fiscal.compass.domain.model.rbac.Permission
import com.fiscal.compass.data.remote.sync.CategorySyncManager
import com.fiscal.compass.data.remote.sync.ExpenseSyncManager
import com.fiscal.compass.data.remote.sync.IncomeSyncManager
import com.fiscal.compass.data.remote.sync.PersonSyncManager
import com.fiscal.compass.data.remote.sync.UserSyncManager
import com.fiscal.compass.domain.model.sync.SyncType
import com.fiscal.compass.domain.usecase.rbac.CheckPermissionUseCase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EnhancedSyncManager @Inject constructor(
    private val auth: FirebaseAuth,
    private val timestampManager: SyncTimestampManager,
    private val dependencyManager: SyncDependencyManager,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val categorySyncManager: CategorySyncManager,
    private val personSyncManager: PersonSyncManager,
    private val userSyncManager: UserSyncManager,
    private val incomeSyncManager: IncomeSyncManager,
    private val expenseSyncManager: ExpenseSyncManager
) {

suspend fun syncAllData(): Flow<Int> = flow {
    emit(0)
    try {
        syncCategories()
        emit(30)
        syncExpenses()
        emit(60)
        syncIncomes()
        emit(90)
        syncPersons()
        emit(100)
    } catch (e: Exception) {
        Log.e(TAG, "Error during syncAllData", e)
        throw e
    }
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
            incomeSyncManager.syncIncomes(userId)
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

    suspend fun syncUsers() {
        if (checkPermissionUseCase(Permission.SYNC_ALL_USERS_DATA)) {
            userSyncManager.downloadAllUsers()
        }
    }

    suspend fun initializeUsers(userId: String) {
        Log.d(TAG, "Initializing users")

        try {
            userSyncManager.downloadAllUsers(true)
            Log.d(TAG, "Users initialization completed")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize users", e)
            throw e
        }
    }

    suspend fun initializeCategories() {
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