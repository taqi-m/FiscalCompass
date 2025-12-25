package com.fiscal.compass.data.remote.sync

import android.util.Log
import com.fiscal.compass.data.local.dao.CategoryDao
import com.fiscal.compass.data.local.dao.ExpenseDao
import com.fiscal.compass.data.local.dao.PersonDao
import com.fiscal.compass.data.local.model.ExpenseEntity
import com.fiscal.compass.data.managers.SyncTimestampManager
import com.fiscal.compass.data.managers.SyncType
import com.fiscal.compass.data.mappers.toDto
import com.fiscal.compass.data.mappers.toEntity
import com.fiscal.compass.data.mappers.toExpenseDto
import com.fiscal.compass.data.mappers.toFirestoreMap
import com.fiscal.compass.data.remote.sync.EnhancedSyncManager.Companion.TAG
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class ExpenseSyncManager @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val timestampManager: SyncTimestampManager,
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao,
    private val personDao: PersonDao
) {
    suspend fun uploadLocalExpenses(userId: String) {
        val unsyncedExpenses = expenseDao.getUnsyncedExpenses(userId)

        if (unsyncedExpenses.isEmpty()) {
            Log.d(TAG, "No local expenses to upload")
            return
        }

        Log.d(TAG, "Uploading ${unsyncedExpenses.size} local expenses")

        val userExpensesRef = firestore.collection("users")
            .document(userId)
            .collection("expenses")

        // Use batch writes for better performance
        var batch = firestore.batch()
        var batchCount = 0
        val expensesToUpdate = mutableListOf<Pair<Long, String>>() // Pair of expenseId and firestoreId

        val currentSyncTime = Timestamp.now().toDate().time

        unsyncedExpenses.forEachIndexed { index, expense ->
            Log.d(TAG, "Processing expense ${index + 1}/${unsyncedExpenses.size}: expenseId=${expense.expenseId}, localId=${expense.localId}, firestoreId=${expense.firestoreId}, needsSync=${expense.needsSync}, isSynced=${expense.isSynced}")

            val categoryFirestoreId = categoryDao.getCategoryByIdIncludeDeleted(expense.categoryId)?.firestoreId
            if (categoryFirestoreId == null) {
                // Skip if category isn't synced yet
                Log.d(TAG, "Skipping expense ${expense.expenseId} as category isn't synced")
                return@forEachIndexed
            }

            var personFirestoreId: String? = null
            if (expense.personId != null) {
                personFirestoreId = personDao.getPersonById(expense.personId)?.firestoreId
                if (personFirestoreId == null) {
                    // Skip if linked person isn't synced yet
                    Log.d(TAG, "Skipping expense ${expense.expenseId} as linked person isn't synced")
                    return@forEachIndexed
                }
            }

            var firestoreId = expense.firestoreId
            Log.d(TAG, "  firestoreId from expense object: $firestoreId")
            
            if (firestoreId.isNullOrBlank()) {
                firestoreId = userExpensesRef.document().id
                Log.d(TAG, "  Generated NEW firestoreId: $firestoreId (this will CREATE a new document)")
            } else {
                Log.d(TAG, "  Using EXISTING firestoreId: $firestoreId (this will UPDATE existing document)")
            }
            
            val expenseData = expense.toDto().copy(
                categoryFirestoreId = categoryFirestoreId,
                personFirestoreId = personFirestoreId
            ).toFirestoreMap(firestoreId, currentSyncTime)

            Log.d(TAG, "  Adding to batch: users/$userId/expenses/$firestoreId")
            val docRef = userExpensesRef.document(firestoreId)
            batch.set(docRef, expenseData)
            expensesToUpdate.add(expense.expenseId to firestoreId)
            batchCount++

            // Firestore batch limit is 500 operations
            if (batchCount >= 500) {
                batch.commit().await()
                Log.d(TAG, "  Batch committed successfully, updating local sync status for $batchCount expenses")
                
                // Update sync status only after successful commit
                expensesToUpdate.forEach { (expenseId, firestoreDocId) ->
                    expenseDao.updateSyncStatus(
                        expenseId = expenseId,
                        firestoreId = firestoreDocId,
                        isSynced = true,
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
            expensesToUpdate.forEach { (expenseId, firestoreDocId) ->
                expenseDao.updateSyncStatus(
                    expenseId = expenseId,
                    firestoreId = firestoreDocId,
                    isSynced = true,
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

        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("expenses")
            .whereGreaterThan("updatedAt", Timestamp(Date(lastSyncTime)))
            .get()
            .await()

        Log.d(TAG, "Found ${snapshot.documents.size} remote expenses to sync")

        var processedCount = 0
        var latestRemoteTimestamp = lastSyncTime

        snapshot.documents.forEach { doc ->
            val catFirestoreId = doc.getString("categoryFirestoreId")
            if (catFirestoreId.isNullOrEmpty()) {
                // Skip documents without a valid localId
                return@forEach
            }
            val categoryId = categoryDao.getCategoryByFirestoreId(catFirestoreId)?.categoryId
            if (categoryId == 0L || categoryId == null) {
                Log.d(TAG, "Skipping expense ${doc.id} as linked category is missing locally")
                return@forEach
            }

            var personId: Long? = null
            val personFirestoreId = doc.getString("personFirestoreId")
            if (!personFirestoreId.isNullOrBlank()) {
                val localPerson = personDao.getPersonByFirestoreId(personFirestoreId)
                if (localPerson == null) {
                    Log.d(TAG, "Skipping expense ${doc.id} as linked person is missing locally")
                    return@forEach
                }
                personId = localPerson.personId
            }

            val remoteExpense = doc.toExpenseDto()?.toEntity()?.copy(
                categoryId = categoryId,
                personId = personId
            )

            if (remoteExpense == null) {
                Log.d(TAG, "Failed to parse expense ${doc.id}, skipping...")
                return@forEach
            }

            // Track the latest timestamp for incremental sync
            latestRemoteTimestamp = maxOf(latestRemoteTimestamp, remoteExpense.updatedAt)

            // Check if expense already exists locally (use localId as it's the stable identifier)
            val existingExpense = expenseDao.getExpenseByLocalId(remoteExpense.localId)

            if (existingExpense != null) {
                // Update existing expense
                val resolvedExpense = resolveConflict(existingExpense, remoteExpense)
                expenseDao.update(resolvedExpense)
            } else {
                // Insert new expense
                expenseDao.insert(remoteExpense)
            }

            processedCount++
        }

        Log.d(TAG, "Processed $processedCount remote expenses")

        // Update the sync timestamp to the latest processed timestamp
        if (latestRemoteTimestamp > lastSyncTime) {
            timestampManager.updateLastSyncTimestamp(SyncType.EXPENSES, latestRemoteTimestamp)
        }
    }

    private fun resolveConflict(
        local: ExpenseEntity,
        remote: ExpenseEntity
    ): ExpenseEntity {
        return if (local.updatedAt >= remote.updatedAt) {
            // Local is newer or same, keep local but ensure it's marked as synced
            local.copy(
                firestoreId = local.firestoreId ?: remote.firestoreId, // Preserve firestoreId
                isSynced = true,
                needsSync = false,
                lastSyncedAt = System.currentTimeMillis()
            )
        } else {
            // Remote is newer, use remote data
            remote.copy(
                expenseId = local.expenseId, // Preserve local primary key
                firestoreId = local.firestoreId ?: remote.firestoreId, // Preserve firestoreId
                isSynced = true,
                needsSync = false,
                lastSyncedAt = System.currentTimeMillis()
            )
        }
    }

}