package com.fiscal.compass.data.remote.sync

import android.util.Log
import com.fiscal.compass.data.local.dao.ExpenseDao
import com.fiscal.compass.data.local.model.ExpenseEntity
import com.fiscal.compass.domain.sync.SyncTimestampManager
import com.fiscal.compass.data.mappers.toDto
import com.fiscal.compass.data.mappers.toEntity
import com.fiscal.compass.data.mappers.toExpenseDto
import com.fiscal.compass.data.mappers.withSyncTimestamp
import com.fiscal.compass.domain.model.sync.SyncType
import com.fiscal.compass.domain.sync.EnhancedSyncManager.Companion.TAG
import com.fiscal.compass.domain.sync.strategy.SyncQueryStrategy
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class ExpenseSyncManager @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val timestampManager: SyncTimestampManager,
    private val expenseDao: ExpenseDao,
    private val syncQueryStrategy: SyncQueryStrategy
) {

    suspend fun uploadLocalExpenses(userId: String) {
        // Use permission-based filtering to determine which expenses to upload
        val shouldFilterByUserId = syncQueryStrategy.shouldFilterByUserId()
        val unsyncedExpenses = if (shouldFilterByUserId) {
            // Employee: Only upload own expenses
            Log.d(TAG, "Fetching unsynced expenses for user: $userId (EMPLOYEE)")
            expenseDao.getUnsyncedExpenses(userId)
        } else {
            // Admin: Upload all users' expenses
            Log.d(TAG, "Fetching all unsynced expenses (ADMIN)")
            expenseDao.getUnsyncedExpenses()
        }

        if (unsyncedExpenses.isEmpty()) {
            Log.d(TAG, "No local expenses to upload")
            return
        }

        Log.d(TAG, "Uploading ${unsyncedExpenses.size} local expenses")

        val userExpensesRef = firestore.collection("expenses")

        // Use batch writes for better performance
        var batch = firestore.batch()
        var batchCount = 0
        val expensesToUpdate = mutableListOf<String>() // List of expenseIds to mark as synced

        val currentSyncTime = Timestamp.now().toDate().time

        unsyncedExpenses.forEachIndexed { index, expense ->
            Log.d(TAG, "Processing expense ${index + 1}/${unsyncedExpenses.size}: expenseId=${expense.expenseId}, needsSync=${expense.needsSync}, isSynced=${expense.isSynced}")

            // Use expenseId as the Firestore document ID
            val firestoreDocId = expense.expenseId
            Log.d(TAG, "  Using expenseId as document ID: $firestoreDocId")

            // Convert to DTO and update sync timestamp
            val expenseDto = expense.toDto().withSyncTimestamp(currentSyncTime)

            Log.d(TAG, "  Adding to batch: expenses/$firestoreDocId")
            val docRef = userExpensesRef.document(firestoreDocId)
            batch.set(docRef, expenseDto) // Direct DTO serialization
            expensesToUpdate.add(expense.expenseId)
            batchCount++

            // Firestore batch limit is 500 operations
            if (batchCount >= 500) {
                batch.commit().await()
                Log.d(TAG, "  Batch committed successfully, updating local sync status for $batchCount expenses")
                
                // Update sync status only after successful commit
                expensesToUpdate.forEach { expenseId ->
                    expenseDao.updateSyncStatus(
                        expenseId = expenseId,
                        lastSyncedAt = currentSyncTime
                    )
                }
                
                batch = firestore.batch()
                batchCount = 0
                expensesToUpdate.clear()
            }
        }

        // Commit remaining operations
        if (batchCount > 0) {
            batch.commit().await()
            Log.d(TAG, "  Final batch committed successfully, updating local sync status for $batchCount expenses")
            
            // Update sync status only after successful commit
            expensesToUpdate.forEach { expenseId ->
                expenseDao.updateSyncStatus(
                    expenseId = expenseId,
                    lastSyncedAt = currentSyncTime
                )
            }
        }

        Log.d(TAG, "Successfully uploaded ${unsyncedExpenses.size} expenses")
    }

    suspend fun downloadRemoteExpenses(userId: String, isInitialization: Boolean = false) {
        val lastSyncTime = if (isInitialization) {
            0L // Download all data during initialization
        } else {
            timestampManager.getLastSyncTimestamp(SyncType.EXPENSES, userId)
        }
        Log.d(TAG, "Last sync time for expenses: ${Date(lastSyncTime)}")

        // Build base query
        val baseQuery = firestore.collection("expenses")

        // Apply permission-based filtering dynamically using injected strategy
        val query = syncQueryStrategy.buildDownloadQuery(baseQuery, userId)
            .whereGreaterThan("updatedAt", Timestamp(Date(lastSyncTime)))

        val snapshot = query.get().await()

        Log.d(TAG, "Found ${snapshot.documents.size} remote expenses to sync")

        var processedCount = 0
        var latestRemoteTimestamp = lastSyncTime

        snapshot.documents.forEach { doc ->
            // Use document ID as expenseId
            val expenseId = doc.id
            Log.d(TAG, "Processing remote expense with ID: $expenseId")

            val remoteExpense = doc.toExpenseDto()?.toEntity()?.copy(
                expenseId = expenseId // Ensure expenseId matches document ID
            )

            if (remoteExpense == null) {
                Log.d(TAG, "Failed to parse expense $expenseId, skipping...")
                return@forEach
            }

            // Track the latest timestamp for incremental sync
            latestRemoteTimestamp = maxOf(latestRemoteTimestamp, remoteExpense.updatedAt)

            // Check if expense already exists locally using expenseId
            val existingExpense = expenseDao.getExpenseById(expenseId)

            if (existingExpense != null) {
                // Update existing expense
                val resolvedExpense = resolveConflict(existingExpense, remoteExpense)
                expenseDao.update(resolvedExpense)
                Log.d(TAG, "Updated existing expense: $expenseId")
            } else {
                // Insert new expense
                expenseDao.insert(remoteExpense)
                Log.d(TAG, "Inserted new expense: $expenseId")
            }

            processedCount++
        }

        Log.d(TAG, "Processed $processedCount remote expenses")

        // Update the sync timestamp to the latest processed timestamp
        if (latestRemoteTimestamp > lastSyncTime) {
            timestampManager.updateLastSyncTimestamp(SyncType.EXPENSES, latestRemoteTimestamp)
        }
    }

    suspend fun uploadDeletedExpenses(userId: String) {
        // Filter deleted expenses based on permissions using injected strategy
        val shouldFilterByUserId = syncQueryStrategy.shouldFilterByUserId()
        val deletedExpenses = if (shouldFilterByUserId) {
            // Employee: Only sync own deleted expenses
            expenseDao.getUnsyncedDeletedExpenses().filter { it.userId == userId }
        } else {
            // Admin: Sync all deleted expenses
            expenseDao.getUnsyncedDeletedExpenses()
        }

        if (deletedExpenses.isEmpty()) {
            Log.d(TAG, "No deleted expenses to sync")
            return
        }

        Log.d(TAG, "Syncing ${deletedExpenses.size} deleted expenses")

        val userExpensesRef = firestore.collection("expenses")

        // Use batch writes for better performance
        var batch = firestore.batch()
        var batchCount = 0
        val expensesToUpdate = mutableListOf<String>()

        deletedExpenses.forEach { expense ->
            // Use expenseId as the Firestore document ID for deletion
            val firestoreDocId = expense.expenseId
            Log.d(TAG, "Marking expense as deleted in Firestore: $firestoreDocId")

            val docRef = userExpensesRef.document(firestoreDocId)

            // Create DTO with deletion flag and current timestamp
            val deletionDto = expense.toDto().copy(
                isDeleted = true,
                updatedAt = Timestamp.now()
            )

            batch.set(docRef, deletionDto) // Use set instead of update to ensure document exists
            expensesToUpdate.add(expense.expenseId)
            batchCount++

            // Firestore batch limit is 500 operations
            if (batchCount >= 500) {
                batch.commit().await()
                Log.d(TAG, "Deletion batch committed successfully")

                // Mark as synced locally
                expensesToUpdate.forEach { expenseId ->
                    expenseDao.markExpenseAsSynced(expenseId)
                }

                batch = firestore.batch()
                batchCount = 0
                expensesToUpdate.clear()
            }
        }

        // Commit remaining operations
        if (batchCount > 0) {
            batch.commit().await()
            Log.d(TAG, "Final deletion batch committed successfully")

            // Mark as synced locally
            expensesToUpdate.forEach { expenseId ->
                expenseDao.markExpenseAsSynced(expenseId)
            }
        }

        Log.d(TAG, "Successfully synced ${deletedExpenses.size} deleted expenses")
    }

    private fun resolveConflict(
        local: ExpenseEntity,
        remote: ExpenseEntity
    ): ExpenseEntity {
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
                expenseId = local.expenseId, // Preserve local primary key
                isSynced = true,
                needsSync = false,
                lastSyncedAt = System.currentTimeMillis()
            )
        }
    }

}