package com.app.uniqueplant.data.remote.sync

import android.util.Log
import com.app.uniqueplant.data.local.dao.CategoryDao
import com.app.uniqueplant.data.local.dao.IncomeDao
import com.app.uniqueplant.data.local.dao.PersonDao
import com.app.uniqueplant.data.local.model.IncomeEntity
import com.app.uniqueplant.data.managers.SyncTimestampManager
import com.app.uniqueplant.data.managers.SyncType
import com.app.uniqueplant.data.mappers.toDto
import com.app.uniqueplant.data.mappers.toEntity
import com.app.uniqueplant.data.mappers.toFirestoreMap
import com.app.uniqueplant.data.mappers.toIncomeDto
import com.app.uniqueplant.data.remote.sync.EnhancedSyncManager.Companion.TAG
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class IncomeSyncManager @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val timestampManager: SyncTimestampManager,
    private val incomeDao: IncomeDao,
    private val categoryDao: CategoryDao,
    private val personDao: PersonDao
) {
    suspend fun uploadLocalIncomes(userId: String) {
        val unsyncedIncomes = incomeDao.getUnsyncedIncomes(userId)

        if (unsyncedIncomes.isEmpty()) {
            Log.d(TAG, "No local incomes to upload")
            return
        }

        Log.d(TAG, "Uploading ${unsyncedIncomes.size} local incomes")

        val userIncomesRef = firestore.collection("users")
            .document(userId)
            .collection("incomes")

        // Use batch writes for better performance
        var batch = firestore.batch()
        var batchCount = 0

        val currentSyncTime = Timestamp.now().toDate().time

        unsyncedIncomes.forEach { income ->
            val categoryFirestoreId = categoryDao.getCategoryById(income.categoryId)?.firestoreId
            if (categoryFirestoreId == null) {
                // Skip if category isn't synced yet
                Log.d(TAG, "Skipping income ${income.incomeId} as category isn't synced")
                return@forEach
            }

            var personFirestoreId: String? = null
            if (income.personId != null) {
                personFirestoreId = personDao.getPersonById(income.personId)?.firestoreId
                if (personFirestoreId == null) {
                    // Skip if linked person isn't synced yet
                    Log.d(TAG, "Skipping income ${income.incomeId} as linked person isn't synced")
                    return@forEach
                }
            }

            var firestoreDocId = income.firestoreId
            if (firestoreDocId == null) {
                firestoreDocId = userIncomesRef.document().id
            }

            incomeDao.updateSyncStatus(
                incomeId = income.incomeId,
                firestoreId = firestoreDocId,
                isSynced = true,
                lastSyncedAt = System.currentTimeMillis()
            )

            val incomeData = income.toDto().copy(
                categoryFirestoreId = categoryFirestoreId,
                personFirestoreId = personFirestoreId
            ).toFirestoreMap(firestoreDocId, currentSyncTime)

            val docRef = userIncomesRef.document(firestoreDocId)
            batch.set(docRef, incomeData)
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

        Log.d(TAG, "Successfully uploaded ${unsyncedIncomes.size} incomes")
    }

    suspend fun downloadRemoteIncomes(userId: String, isInitialization: Boolean = false) {
        val lastSyncTime = if (isInitialization) {
            0L // Download all data during initialization
        } else {
            timestampManager.getLastSyncTimestamp(SyncType.INCOMES, userId)
        }

        Log.d(TAG, "Last sync time for incomes: ${Date(lastSyncTime)}")

        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("incomes")
            .whereGreaterThan("updatedAt", Timestamp(Date(lastSyncTime)))
            .get()
            .await()

        Log.d(TAG, "Found ${snapshot.documents.size} remote incomes to sync")

        var processedCount = 0
        var latestRemoteTimestamp = lastSyncTime

        snapshot.documents.forEach { doc ->
            val categoryFirestoreId = doc.getString("categoryFirestoreId")
            if (categoryFirestoreId.isNullOrEmpty()) {
                // Skip documents without a valid localId
                return@forEach
            }
            val categoryId = categoryDao.getCategoryByFirestoreId(categoryFirestoreId)?.categoryId
            if (categoryId == 0L || categoryId == null) {
                Log.d(TAG, "Skipping income ${doc.id} as category is missing locally")
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

            val remoteIncome = doc.toIncomeDto()?.toEntity()?.copy(
                categoryId = categoryId,
                personId = personId
            )

            if (remoteIncome == null) {
                Log.d(TAG, "Skipping invalid income document: ${doc.id}")
                return@forEach
            }


            // Track the latest timestamp for incremental sync
            latestRemoteTimestamp = maxOf(latestRemoteTimestamp, remoteIncome.updatedAt)

            // Check if income already exists locally
            val existingIncome = incomeDao.getIncomeByLocalId(remoteIncome.localId)

            if (existingIncome != null) {
                // Update existing income
                val resolvedIncome = resolveConflict(existingIncome, remoteIncome)
                incomeDao.update(resolvedIncome)
            } else {
                // Insert new income
                incomeDao.insert(remoteIncome)
            }

            processedCount++
        }

        Log.d(TAG, "Processed $processedCount remote incomes")

        // Update the sync timestamp to the latest processed timestamp
        if (latestRemoteTimestamp > lastSyncTime) {
            timestampManager.updateLastSyncTimestamp(SyncType.INCOMES, latestRemoteTimestamp)
        }
    }

    private fun resolveConflict(
        local: IncomeEntity,
        remote: IncomeEntity
    ): IncomeEntity {
        return if (local.updatedAt >= remote.updatedAt) {
            // Local is newer or same, keep local
            local
        } else {
            // Remote is newer, use remote
            remote.copy(incomeId = local.incomeId) // Preserve local primary key
        }
    }

}