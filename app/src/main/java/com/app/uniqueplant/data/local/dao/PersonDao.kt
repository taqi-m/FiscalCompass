package com.app.uniqueplant.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.uniqueplant.data.local.model.PersonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {

    @androidx.room.Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(person: PersonEntity): Long

    @androidx.room.Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(persons: List<PersonEntity>)

    @Query("SELECT * FROM persons")
    suspend fun getAll(): List<PersonEntity>

    @Query("SELECT * FROM persons WHERE personId = :id LIMIT 1")
    suspend fun getPersonById(id: Long?): PersonEntity?

    @Query("SELECT * FROM persons")
    fun getAllWithFlow(): Flow<List<PersonEntity>>

    @Query("SELECT * FROM persons WHERE personId = :id")
    suspend fun getById(id: Long): PersonEntity?

    @Query("SELECT * FROM persons WHERE personType = :type")
    suspend fun getByPersonType(type: String): List<PersonEntity>
    @Query("SELECT * FROM persons WHERE personType = :type")
    fun getByPersonTypeWithFlow(type: String): Flow<List<PersonEntity>>

    @Update
    suspend fun update(person: PersonEntity): Int

    @Delete
    suspend fun delete(person: PersonEntity)

    @Query("DELETE FROM persons")
    suspend fun deleteAll()

    suspend fun getPersonLocalId(personId: Long?): String? {
        if (personId == null) return null
        return runCatching {
            val person = getById(personId)
            person?.localId
        }.getOrNull()
    }

    suspend fun getPersonFirestoreId(personId: Long?): String? {
        if (personId == null) return null
        return runCatching {
            val person = getById(personId)
            person?.firestoreId
        }.getOrNull()
    }

    @Query("SELECT personId FROM persons WHERE localId = :localId LIMIT 1")
    suspend fun getPersonIdByLocalId(localId: String?): Long?



    /** Sync timestamp queries
     *  These help in determining what data needs to be synced
     *  between local database and remote server.
     */

    @Query("SELECT * FROM persons WHERE needsSync = 1")
    suspend fun getUnsyncedPersons(): List<PersonEntity>
    @Query(
        """
    SELECT MIN(createdAt)
    FROM persons
    WHERE needsSync = 1
    """)
    suspend fun getOldestUnsyncedPersonTimestamp(): Long?

    @Query(
        """
    SELECT MAX(lastSyncedAt)
    FROM persons
    WHERE lastSyncedAt IS NOT NULL
    """)
    suspend fun getLatestSyncedPersonTimestamp(): Long?

    @Query(
        """
    SELECT MAX(updatedAt)
    FROM persons
    """)
    suspend fun getLatestLocalPersonUpdateTimestamp(): Long?

    @Query(
        """
    SELECT COUNT(*)
    FROM persons
    WHERE updatedAt > :timestamp
    """)
    suspend fun getUpdatedPersonsSince(timestamp: Long): Int

    @Query("SELECT * FROM persons WHERE firestoreId = :id LIMIT 1")
    suspend fun getPersonByFirestoreId(id: String): PersonEntity?


}
