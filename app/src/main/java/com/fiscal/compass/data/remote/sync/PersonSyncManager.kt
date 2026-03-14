package com.fiscal.compass.data.remote.sync

import android.util.Log
import com.fiscal.compass.data.local.dao.PersonDao
import com.fiscal.compass.data.remote.model.PersonDto
import com.fiscal.compass.domain.sync.SyncTimestampManager
import com.fiscal.compass.data.mappers.toFirestoreMap
import com.fiscal.compass.data.mappers.toPersonDto
import com.fiscal.compass.data.mappers.toPersonEntity
import com.fiscal.compass.domain.model.sync.SyncType
import com.fiscal.compass.domain.sync.EnhancedSyncManager.Companion.TAG
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
                    val personMap = person.toFirestoreMap(syncTime = currentSyncTime)

                    Log.d(TAG, "Uploading person '${person.name}' with personId=${person.personId}")

                    // Use personId as Firestore document ID with stable field names
                    globalPersonRef.document(person.personId).set(personMap).await()

                    // Update local entity with sync status
                    personDao.update(
                        person.copy(
                            isSynced = true,
                            needsSync = false,
                            lastSyncedAt = currentSyncTime
                        )
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error uploading person ${person.name}", e)
                }
            }

            timestampManager.updateLastSyncTimestamp(SyncType.PERSONS)
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

            // Process all persons using direct DTO deserialization
            globalPersons.documents.forEach { doc ->
                try {
                    val personDto = doc.toPersonDto()
                    if (personDto != null) {
                        processRemotePerson(personDto)
                    }
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

    private suspend fun processRemotePerson(personDto: PersonDto) {
        // Convert DTO to entity using mapper
        val parsedPerson = personDto.toPersonEntity()
        val existingPerson = personDao.getPersonById(parsedPerson.personId)

        if (existingPerson == null) {
            // New person from remote - insert directly
            personDao.insert(parsedPerson)
            return
        }

        // Update only if remote version is newer
        val remoteUpdateTime = parsedPerson.updatedAt
        val localUpdateTime = existingPerson.updatedAt

        if (remoteUpdateTime > localUpdateTime) {
            personDao.update(
                parsedPerson.copy(
                    isSynced = true,
                    needsSync = false
                )
            )
        }
    }
}