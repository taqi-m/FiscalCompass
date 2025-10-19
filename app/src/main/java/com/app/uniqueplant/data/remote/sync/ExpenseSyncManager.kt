package com.app.uniqueplant.data.remote.sync

import android.util.Log
import com.app.uniqueplant.data.local.dao.CategoryDao
import com.app.uniqueplant.data.local.dao.ExpenseDao
import com.app.uniqueplant.data.local.dao.PersonDao
import com.app.uniqueplant.data.local.model.ExpenseEntity
import com.app.uniqueplant.data.managers.SyncTimestampManager
import com.app.uniqueplant.data.managers.SyncType
import com.app.uniqueplant.data.mappers.toDto
import com.app.uniqueplant.data.mappers.toEntity
import com.app.uniqueplant.data.mappers.toExpenseDto
import com.app.uniqueplant.data.mappers.toFirestoreMap
import com.app.uniqueplant.data.remote.sync.EnhancedSyncManager.Companion.TAG
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

        val currentSyncTime = Timestamp.now().toDate().time



        unsyncedExpenses.forEach { expense ->

            val categoryFirestoreId = categoryDao.getCategoryById(expense.categoryId)?.firestoreId
            if (categoryFirestoreId == null) {
                // Skip if category isn't synced yet
                Log.d(TAG, "Skipping expense ${expense.expenseId} as category isn't synced")
                return@forEach
            }

            var personFirestoreId: String? = null
            if (expense.personId != null) {
                personFirestoreId = personDao.getPersonById(expense.personId)?.firestoreId
                if (personFirestoreId == null) {
                    // Skip if linked person isn't synced yet
                    Log.d(TAG, "Skipping expense ${expense.expenseId} as linked person isn't synced")
                    return@forEach
                }
            }


            var firestoreId = expense.firestoreId
            if (firestoreId == null) {
                firestoreId = userExpensesRef.document().id
            }

            // Update local sync status immediately
            // This ensures that even if some part of the sync fails,
            // successfully processed items are not re-processed.
            expenseDao.updateSyncStatus(
                expenseId = expense.expenseId,
                firestoreId = firestoreId,
                isSynced = true,
                lastSyncedAt = currentSyncTime
            )
            val expenseData = expense.toDto().copy(
                categoryFirestoreId = categoryFirestoreId,
                personFirestoreId = personFirestoreId
            ).toFirestoreMap(firestoreId, currentSyncTime)

            val docRef = userExpensesRef.document(firestoreId)
            batch.set(docRef, expenseData)
            batchCount++

            // Firestore batch limit is 500 operations
            if (batchCount >= 500) {
                batch.commit().await()
                batch = firestore.batch()
                batchCount = 0
            }

        }

        // Commit remaining operations
        if (batchCount > 0) {
            batch.commit().await()
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

            // Check if expense already exists locally
            val existingExpense = expenseDao.getExpenseByFirestoreId(remoteExpense.firestoreId!!)

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
                isSynced = true,
                needsSync = false,
                lastSyncedAt = System.currentTimeMillis()
            )
        } else {
            // Remote is newer, use remote data
            remote.copy(
                expenseId = local.expenseId, // Preserve local primary key
                isSynced = true,
                needsSync = false,
                lastSyncedAt = System.currentTimeMillis()
            )
        }
    }

}