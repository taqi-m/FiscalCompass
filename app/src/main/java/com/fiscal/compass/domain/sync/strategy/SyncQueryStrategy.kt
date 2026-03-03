package com.fiscal.compass.domain.sync.strategy

import com.google.firebase.firestore.Query

/**
 * Strategy interface for building Firestore queries with role-based filtering.
 *
 * This strategy evaluates permissions at runtime to handle user switching scenarios
 * (e.g., Admin logout → Employee login in the same session).
 *
 * Key principles:
 * - No cached permission state - always evaluate at call time
 * - Supports multi-tenant data access based on SYNC_ALL_USERS_DATA permission
 * - Used by sync managers to build queries dynamically
 */
interface SyncQueryStrategy {

    /**
     * Builds a Firestore query with appropriate user filtering based on permissions.
     *
     * If user has SYNC_ALL_USERS_DATA permission, returns query without userId filter.
     * If user has only SYNC_OWN_DATA permission, applies .whereEqualTo("userId", userId).
     *
     * @param baseQuery The initial Firestore query (e.g., firestore.collection("expenses"))
     * @param userId The current user's ID
     * @return Query with appropriate filtering applied
     */
    suspend fun buildDownloadQuery(baseQuery: Query, userId: String): Query

    /**
     * Determines if data should be filtered by userId based on current permissions.
     *
     * @return true if only own data should be synced, false if all users' data is accessible
     */
    suspend fun shouldFilterByUserId(): Boolean
}

