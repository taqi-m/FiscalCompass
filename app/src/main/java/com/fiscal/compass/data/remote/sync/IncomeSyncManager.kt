package com.fiscal.compass.data.remote.sync

import android.util.Log
import com.fiscal.compass.data.local.dao.CategoryDao
import com.fiscal.compass.data.local.dao.IncomeDao
import com.fiscal.compass.data.local.dao.PersonDao
import com.fiscal.compass.data.local.model.IncomeEntity
import com.fiscal.compass.data.managers.SyncTimestampManager
import com.fiscal.compass.data.managers.SyncType
import com.fiscal.compass.data.mappers.toDto
import com.fiscal.compass.data.mappers.toEntity
import com.fiscal.compass.data.mappers.toFirestoreMap
import com.fiscal.compass.data.mappers.toIncomeDto
import com.fiscal.compass.data.remote.sync.EnhancedSyncManager.Companion.TAG
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
        val incomesToUpdate = mutableListOf<Pair<Long, String>>() // Pair of incomeId and firestoreId

        val currentSyncTime = Timestamp.now().toDate().time

        unsyncedIncomes.forEachIndexed { index, income ->
            Log.d(TAG, "Processing income ${index + 1}/${unsyncedIncomes.size}: incomeId=${income.incomeId}, localId=${income.localId}, firestoreId=${income.firestoreId}, needsSync=${income.needsSync}, isSynced=${income.isSynced}")
            
            val categoryFirestoreId = categoryDao.getCategoryById(income.categoryId)?.firestoreId
            if (categoryFirestoreId == null) {
                // Skip if category isn't synced yet
                Log.d(TAG, "Skipping income ${income.incomeId} as category isn't synced")
                return@forEachIndexed
            }

            var personFirestoreId: String? = null
            if (income.personId != null) {
                personFirestoreId = personDao.getPersonById(income.personId)?.firestoreId
                if (personFirestoreId == null) {
                    // Skip if linked person isn't synced yet
                    Log.d(TAG, "Skipping income ${income.incomeId} as linked person isn't synced")
                    return@forEachIndexed
                }
            }

            var firestoreDocId = income.firestoreId
            Log.d(TAG, "  firestoreDocId from income object: $firestoreDocId")
            
            if (firestoreDocId.isNullOrBlank()) {
                firestoreDocId = userIncomesRef.document().id
                Log.d(TAG, "  Generated NEW firestoreDocId: $firestoreDocId (this will CREATE a new document)")
            } else {
                Log.d(TAG, "  Using EXISTING firestoreDocId: $firestoreDocId (this will UPDATE existing document)")
            }

            val incomeData = income.toDto().copy(
                categoryFirestoreId = categoryFirestoreId,
                personFirestoreId = personFirestoreId
            ).toFirestoreMap(firestoreDocId, currentSyncTime)

            Log.d(TAG, "  Adding to batch: users/$userId/incomes/$firestoreDocId")
            val docRef = userIncomesRef.document(firestoreDocId)
            batch.set(docRef, incomeData)
            incomesToUpdate.add(income.incomeId to firestoreDocId)
            batchCount++

            // Firestore batch limit is 500 operations
            if (batchCount >= 500) {
                batch.commit().await()
                Log.d(TAG, "  Batch committed successfully, updating local sync status for $batchCount incomes")
                
                // Update sync status only after successful commit
                incomesToUpdate.forEach { (incomeId, firestoreId) ->
                    incomeDao.updateSyncStatus(
                        incomeId = incomeId,
                        firestoreId = firestoreId,
                        isSynced = true,
                        lastSyncedAt = System.currentTimeMillis()
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
            incomesToUpdate.forEach { (incomeId, firestoreId) ->
                incomeDao.updateSyncStatus(
                    incomeId = incomeId,
                    firestoreId = firestoreId,
                    isSynced = true,
                    lastSyncedAt = System.currentTimeMillis()
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

        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("incomes")
            .whereGreaterThan("updatedAt", Timestamp(Date(lastSyncTime)))
            .get()
            .await()

        Log.d(TAG, "Found ${snapshot.documents.size} remote incomes to sync")

        var processedCount = 0
        var latestRemoteTimestamp = lastSyncTime

        snapshot.documents.forEachIndexed { index, doc ->
            Log.d(TAG, "Processing income ${index + 1}/${snapshot.documents.size}: docId=${doc.id}")
            
            val categoryFirestoreId = doc.getString("categoryFirestoreId")
            Log.d(TAG, "  categoryFirestoreId from Firestore: $categoryFirestoreId")
            
            if (categoryFirestoreId.isNullOrEmpty()) {
                Log.d(TAG, "  SKIPPED: categoryFirestoreId is null or empty")
                return@forEachIndexed
            }
            
            val localCategory = categoryDao.getCategoryByFirestoreId(categoryFirestoreId)
            val categoryId = localCategory?.categoryId
            Log.d(TAG, "  Local category lookup: categoryId=$categoryId (found=${localCategory != null})")
            
            if (categoryId == 0L || categoryId == null) {
                Log.d(TAG, "  SKIPPED: category missing locally (firestoreId=$categoryFirestoreId)")
                return@forEachIndexed
            }

            var personId: Long? = null
            val personFirestoreId = doc.getString("personFirestoreId")
            Log.d(TAG, "  personFirestoreId from Firestore: $personFirestoreId")
            
            if (!personFirestoreId.isNullOrBlank()) {
                val localPerson = personDao.getPersonByFirestoreId(personFirestoreId)
                personId = localPerson?.personId
                Log.d(TAG, "  Local person lookup: personId=$personId (found=${localPerson != null})")
                
                if (localPerson == null) {
                    Log.d(TAG, "  SKIPPED: linked person missing locally (firestoreId=$personFirestoreId)")
                    return@forEachIndexed
                }
            }

            val remoteIncomeDto = doc.toIncomeDto()
            Log.d(TAG, "  Income DTO created: ${remoteIncomeDto != null}")
            
            val remoteIncome = remoteIncomeDto?.toEntity()?.copy(
                categoryId = categoryId,
                personId = personId
            )

            if (remoteIncome == null) {
                Log.d(TAG, "  SKIPPED: invalid income document")
                return@forEachIndexed
            }
            
            Log.d(TAG, "  Remote income: localId=${remoteIncome.localId}, categoryId=${remoteIncome.categoryId}, personId=${remoteIncome.personId}, userId=${remoteIncome.userId}")

            // Track the latest timestamp for incremental sync
            latestRemoteTimestamp = maxOf(latestRemoteTimestamp, remoteIncome.updatedAt)

            // Check if income already exists locally
            val existingIncome = incomeDao.getIncomeByLocalId(remoteIncome.localId)
            Log.d(TAG, "  Existing income: ${if (existingIncome != null) "found (id=${existingIncome.incomeId})" else "not found"}")

            try {
                if (existingIncome != null) {
                    Log.d(TAG, "  Updating existing income...")
                    val resolvedIncome = resolveConflict(existingIncome, remoteIncome)
                    incomeDao.update(resolvedIncome)
                    Log.d(TAG, "  Updated successfully")
                } else {
                    Log.d(TAG, "  Inserting new income...")
                    incomeDao.insert(remoteIncome)
                    Log.d(TAG, "  Inserted successfully")
                }
                processedCount++
            } catch (e: Exception) {
                Log.e(TAG, "  FAILED to insert/update income: ${e.message}")
                Log.e(TAG, "  Income details: categoryId=$categoryId, personId=$personId, userId=${remoteIncome.userId}")
                throw e
            }
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
            // Local is newer or same, keep local but ensure sync status is correct
            local.copy(
                firestoreId = local.firestoreId ?: remote.firestoreId, // Preserve firestoreId
                isSynced = true,
                needsSync = false,
                lastSyncedAt = System.currentTimeMillis()
            )
        } else {
            // Remote is newer, use remote data but preserve local keys
            remote.copy(
                incomeId = local.incomeId, // Preserve local primary key
                firestoreId = local.firestoreId ?: remote.firestoreId, // Preserve firestoreId
                isSynced = true,
                needsSync = false,
                lastSyncedAt = System.currentTimeMillis()
            )
        }
    }

}