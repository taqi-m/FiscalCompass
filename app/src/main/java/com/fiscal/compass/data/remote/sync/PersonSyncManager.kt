package com.fiscal.compass.data.remote.sync

import android.util.Log
import com.fiscal.compass.data.local.dao.PersonDao
import com.fiscal.compass.data.managers.SyncTimestampManager
import com.fiscal.compass.data.managers.SyncType
import com.fiscal.compass.data.mappers.toDto
import com.fiscal.compass.data.mappers.toFirestoreMap
import com.fiscal.compass.data.mappers.toPersonEntity
import com.fiscal.compass.data.remote.sync.EnhancedSyncManager.Companion.TAG
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class PersonSyncManager @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val timestampManager: SyncTimestampManager,
    private val personDao: PersonDao
) {
    suspend fun uploadLocalPersons() {
        val unsyncedPersons = personDao.getUnsyncedPersons()
        if (unsyncedPersons.isEmpty()) {
            Log.d(TAG, "No local persons to upload")
            return
        }

        Log.d(TAG, "Uploading ${unsyncedPersons.size} local persons")

        try {
            val globalPersonRef = firestore.collection("globalPersons")
            val currentSyncTime = System.currentTimeMillis()
            unsyncedPersons.forEach { person ->
                try {
                    val personDto = person.toDto()
                    var firestoreDocId = person.firestoreId

                    if (firestoreDocId == null) {
                        Log.d(TAG, "Person ${person.name} has no Firestore ID, generating new one")
                        firestoreDocId = globalPersonRef.document().id
                    }

                    Log.d(TAG, "Uploading person '${person.name}' with firestoreId=$firestoreDocId")

                    val personMap = personDto.toFirestoreMap(
                        firestoreId = firestoreDocId,
                        syncTime = currentSyncTime
                    )

                    globalPersonRef.document(firestoreDocId).set(personMap).await()

                    // Update local entity with sync status
                    personDao.update(
                        person.copy(
                            firestoreId = firestoreDocId,
                            isSynced = true,
                            needsSync = false,
                            lastSyncedAt = currentSyncTime
                        )
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error uploading person ${person.name}", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in uploadLocalPersons", e)
            throw e
        }
    }

    suspend fun downloadRemotePersons() {
        val lastSyncTime = timestampManager.getPersonsLastSyncTimestamp()

        try {
            // Download global persons that were updated after our last sync
            val globalPersons = firestore.collection("globalPersons")
                .whereGreaterThan("updatedAt", Timestamp(Date(lastSyncTime)))
                .get()
                .await()

            Log.d(TAG, "Found ${globalPersons.documents.size} remote persons to sync")

            globalPersons.documents.forEach { doc ->
                try {
                    processRemotePerson(doc.data ?: return@forEach)
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing person from Firestore", e)
                }
            }

            timestampManager.updateLastSyncTimestamp(SyncType.PERSONS)
        } catch (e: Exception) {
            Log.e(TAG, "Error in downloadRemotePersons", e)
            throw e
        }
    }

    private suspend fun processRemotePerson(personData: Map<String, Any>) {
        val parsedPerson = personData.toPersonEntity()
        val existingPerson = parsedPerson.firestoreId?.let {
            personDao.getPersonByFirestoreId(it)
        }

        if (existingPerson == null) {
            personDao.insert(parsedPerson)
            return
        }

        val remoteUpdateTime = parsedPerson.updatedAt
        val localUpdateTime = existingPerson.updatedAt

        if (remoteUpdateTime > localUpdateTime) {
            personDao.update(
                parsedPerson.copy(
                    personId = existingPerson.personId,
                    isSynced = true,
                    needsSync = false,
                )
            )
        }
    }
}