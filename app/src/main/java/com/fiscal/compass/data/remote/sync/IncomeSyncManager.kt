package com.fiscal.compass.data.remote.sync

import android.util.Log
import com.fiscal.compass.data.local.dao.IncomeDao
import com.fiscal.compass.data.local.model.IncomeEntity
import com.fiscal.compass.domain.sync.SyncTimestampManager
import com.fiscal.compass.data.mappers.toEntity
import com.fiscal.compass.data.mappers.toFirestoreMap
import com.fiscal.compass.data.mappers.toIncomeDto
import com.fiscal.compass.domain.model.sync.SyncType
import com.fiscal.compass.domain.sync.EnhancedSyncManager.Companion.TAG
import com.fiscal.compass.domain.sync.strategy.SyncQueryStrategy
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class IncomeSyncManager @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val timestampManager: SyncTimestampManager,
    private val incomeDao: IncomeDao,
    private val syncQueryStrategy: SyncQueryStrategy
) {
    /**
     * Performs a complete sync of incomes for a user.
     * This includes uploading local changes and downloading remote changes.
     */
    suspend fun syncIncomes(userId: String, isInitialization: Boolean = false) {
        try {
            Log.d(TAG, "Starting income sync for user: $userId, initialization: $isInitialization")

            // First, upload local incomes (including new and modified)
            uploadLocalIncomes(userId)

            // Then, upload deleted incomes
            uploadDeletedIncomes(userId)

            // Finally, download remote changes
            downloadRemoteIncomes(userId, isInitialization)

            Log.d(TAG, "Completed income sync for user: $userId")
        } catch (e: Exception) {
            Log.e(TAG, "Error during income sync for user $userId", e)
            throw e
        }
    }

    suspend fun uploadLocalIncomes(userId: String) {
        // Use permission-based filtering to determine which incomes to upload
        val shouldFilterByUserId = syncQueryStrategy.shouldFilterByUserId()
        val unsyncedIncomes = if (shouldFilterByUserId) {
            // Employee: Only upload own incomes
            Log.d(TAG, "Fetching unsynced incomes for user: $userId (EMPLOYEE)")
            incomeDao.getUnsyncedIncomes(userId)
        } else {
            // Admin: Upload all users' incomes
            Log.d(TAG, "Fetching all unsynced incomes (ADMIN)")
            incomeDao.getUnsyncedIncomes()
        }

        if (unsyncedIncomes.isEmpty()) {
            Log.d(TAG, "No local incomes to upload")
            return
        }

        Log.d(TAG, "Uploading ${unsyncedIncomes.size} local incomes")

        val userIncomesRef = firestore.collection("incomes")

        // Use batch writes for better performance
        var batch = firestore.batch()
        var batchCount = 0
        val incomesToUpdate = mutableListOf<String>() // List of incomeIds to mark as synced

        val currentSyncTime = Timestamp.now().toDate().time

        unsyncedIncomes.forEachIndexed { index, income ->
            Log.d(TAG, "Processing income ${index + 1}/${unsyncedIncomes.size}: incomeId=${income.incomeId}, needsSync=${income.needsSync}, isSynced=${income.isSynced}")

            // Use incomeId as the Firestore document ID
            val firestoreDocId = income.incomeId
            Log.d(TAG, "  Using incomeId as document ID: $firestoreDocId")

            // Convert to stable map and update sync timestamp
            val incomeMap = income.toFirestoreMap(syncTime = currentSyncTime)

            Log.d(TAG, "  Adding to batch: incomes/$firestoreDocId")
            val docRef = userIncomesRef.document(firestoreDocId)
            batch.set(docRef, incomeMap) // Stable map serialization
            incomesToUpdate.add(income.incomeId)
            batchCount++

            // Firestore batch limit is 500 operations
            if (batchCount >= 500) {
                batch.commit().await()
                Log.d(TAG, "  Batch committed successfully, updating local sync status for $batchCount incomes")

                // Update sync status only after successful commit
                incomesToUpdate.forEach { incomeId ->
                    incomeDao.updateSyncStatus(
                        incomeId = incomeId,
                        lastSyncedAt = currentSyncTime
                    )
                }

                batch = firestore.batch()
                batchCount = 0
                incomesToUpdate.clear()
            }
        }

        // Commit remaining operations
        if (batchCount > 0) {
            batch.commit().await()
            Log.d(TAG, "  Final batch committed successfully, updating local sync status for $batchCount incomes")

            // Update sync status only after successful commit
            incomesToUpdate.forEach { incomeId ->
                incomeDao.updateSyncStatus(
                    incomeId = incomeId,
                    lastSyncedAt = currentSyncTime
                )
            }
        }

        Log.d(TAG, "Successfully uploaded ${unsyncedIncomes.size} incomes")
    }

    suspend fun downloadRemoteIncomes(userId: String, isInitialization: Boolean = false) {
        val lastSyncTime = if (isInitialization) {
            0L // Download all data during initialization
        } else {
            timestampManager.getLastSyncTimestamp(SyncType.INCOMES, userId)
        }
        Log.d(TAG, "Last sync time for incomes: ${Date(lastSyncTime)}")

        // Build base query
        val baseQuery = firestore.collection("incomes")

        // Apply permission-based filtering dynamically using injected strategy
        val query = syncQueryStrategy.buildDownloadQuery(baseQuery, userId)
            .whereGreaterThan("updatedAt", Timestamp(Date(lastSyncTime)))

        val snapshot = query.get().await()

        Log.d(TAG, "Found ${snapshot.documents.size} remote incomes to sync")

        var processedCount = 0
        var latestRemoteTimestamp = lastSyncTime

        snapshot.documents.forEach { doc ->
            // Use document ID as incomeId
            val incomeId = doc.id
            Log.d(TAG, "Processing remote income with ID: $incomeId")

            val remoteIncome = doc.toIncomeDto()?.toEntity()?.copy(
                incomeId = incomeId // Ensure incomeId matches document ID
            )

            if (remoteIncome == null) {
                Log.d(TAG, "Failed to parse income $incomeId, skipping...")
                return@forEach
            }

            // Track the latest timestamp for incremental sync
            latestRemoteTimestamp = maxOf(latestRemoteTimestamp, remoteIncome.updatedAt)

            // Check if income already exists locally using incomeId
            val existingIncome = incomeDao.getById(incomeId)

            if (existingIncome != null) {
                // Update existing income
                val resolvedIncome = resolveConflict(existingIncome, remoteIncome)
                incomeDao.update(resolvedIncome)
                Log.d(TAG, "Updated existing income: $incomeId")
            } else {
                // Insert new income
                incomeDao.insert(remoteIncome)
                Log.d(TAG, "Inserted new income: $incomeId")
            }

            processedCount++
        }

        Log.d(TAG, "Processed $processedCount remote incomes")

        // Update the sync timestamp to the latest processed timestamp
        if (latestRemoteTimestamp > lastSyncTime) {
            timestampManager.updateLastSyncTimestamp(SyncType.INCOMES, latestRemoteTimestamp)
        }
    }

    suspend fun uploadDeletedIncomes(userId: String) {
        // Filter deleted incomes based on permissions using injected strategy
        val shouldFilterByUserId = syncQueryStrategy.shouldFilterByUserId()
        val deletedIncomes = if (shouldFilterByUserId) {
            // Employee: Only sync own deleted incomes
            incomeDao.getUnsyncedDeletedIncomes().filter { it.userId == userId }
        } else {
            // Admin: Sync all deleted incomes
            incomeDao.getUnsyncedDeletedIncomes()
        }

        if (deletedIncomes.isEmpty()) {
            Log.d(TAG, "No deleted incomes to sync")
            return
        }

        Log.d(TAG, "Syncing ${deletedIncomes.size} deleted incomes")

        val userIncomesRef = firestore.collection("incomes")

        // Use batch writes for better performance
        var batch = firestore.batch()
        var batchCount = 0
        val incomesToUpdate = mutableListOf<String>()

        deletedIncomes.forEach { income ->
            // Use incomeId as the Firestore document ID for deletion
            val firestoreDocId = income.incomeId
            Log.d(TAG, "Marking income as deleted in Firestore: $firestoreDocId")

            val docRef = userIncomesRef.document(firestoreDocId)

            // Create stable map with deletion flag and current timestamp
            val deletionMap = income.toFirestoreMap(
                syncTime = System.currentTimeMillis(),
                forceDeleted = true
            )

            batch.set(docRef, deletionMap) // Use set instead of update to ensure document exists
            incomesToUpdate.add(income.incomeId)
            batchCount++

            // Firestore batch limit is 500 operations
            if (batchCount >= 500) {
                batch.commit().await()
                Log.d(TAG, "Deletion batch committed successfully")

                // Mark as synced locally
                incomesToUpdate.forEach { incomeId ->
                    incomeDao.markIncomeAsSynced(incomeId)
                }

                batch = firestore.batch()
                batchCount = 0
                incomesToUpdate.clear()
            }
        }

        // Commit remaining operations
        if (batchCount > 0) {
            batch.commit().await()
            Log.d(TAG, "Final deletion batch committed successfully")

            // Mark as synced locally
            incomesToUpdate.forEach { incomeId ->
                incomeDao.markIncomeAsSynced(incomeId)
            }
        }

        Log.d(TAG, "Successfully synced ${deletedIncomes.size} deleted incomes")
    }

    private fun resolveConflict(
        local: IncomeEntity,
        remote: IncomeEntity
    ): IncomeEntity {
        return if (local.updatedAt >= remote.updatedAt) {
            // Local is newer or same, keep local but ensure it's marked as synced
            local.copy(
                isSynced = true,
                needsSync = false,
                lastSyncedAt = System.currentTimeMillis()
            )
        } else {
            // Remote is newer, use remote data but preserve local primary key
            remote.copy(
                incomeId = local.incomeId, // Preserve local primary key
                isSynced = true,
                needsSync = false,
                lastSyncedAt = System.currentTimeMillis()
            )
        }
    }
}
