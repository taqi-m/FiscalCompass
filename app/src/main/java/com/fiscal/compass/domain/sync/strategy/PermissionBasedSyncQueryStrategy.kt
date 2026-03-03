package com.fiscal.compass.domain.sync.strategy

import android.util.Log
import com.fiscal.compass.domain.model.rbac.Permission
import com.fiscal.compass.domain.usecase.rbac.CheckPermissionUseCase
import com.google.firebase.firestore.Query
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Permission-based implementation of SyncQueryStrategy.
 *
 * Evaluates SYNC_ALL_USERS_DATA permission at runtime to determine query filtering.
 * This ensures correct behavior when users switch within the same session
 * (e.g., Admin logout → Employee login).
 *
 * Implementation details:
 * - No permission caching - evaluates fresh on every call
 * - Singleton scoped - single instance throughout app lifecycle
 * - Thread-safe through suspend functions and DI
 */
@Singleton
class PermissionBasedSyncQueryStrategy @Inject constructor(
    private val checkPermissionUseCase: CheckPermissionUseCase
) : SyncQueryStrategy {

    override suspend fun buildDownloadQuery(baseQuery: Query, userId: String): Query {
        val canSyncAllUsers = checkPermissionUseCase(Permission.SYNC_ALL_USERS_DATA)

        return if (canSyncAllUsers) {
            // Admin: No userId filter - download all users' data
            Log.d(TAG, "Building query for all users (SYNC_ALL_USERS_DATA permission granted)")
            baseQuery
        } else {
            // Employee: Filter by userId - download only own data
            Log.d(TAG, "Building query filtered by userId=$userId (SYNC_OWN_DATA permission)")
            baseQuery.whereEqualTo("userId", userId)
        }
    }

    override suspend fun shouldFilterByUserId(): Boolean {
        val canSyncAllUsers = checkPermissionUseCase(Permission.SYNC_ALL_USERS_DATA)
        return !canSyncAllUsers // Filter if user cannot sync all users' data
    }

    companion object {
        private const val TAG = "SyncQueryStrategy"
    }
}

