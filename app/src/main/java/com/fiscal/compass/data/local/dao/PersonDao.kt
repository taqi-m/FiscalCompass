package com.fiscal.compass.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fiscal.compass.data.local.model.PersonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(person: PersonEntity): Long

    @Update
    suspend fun update(person: PersonEntity): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(persons: List<PersonEntity>)

    @Query("SELECT * FROM persons WHERE isDeleted = 0")
    suspend fun getAll(): List<PersonEntity>

    @Query("SELECT * FROM persons WHERE personId = :personId AND isDeleted = 0 LIMIT 1")
    suspend fun getPersonById(personId: String?): PersonEntity?

    @Query("SELECT * FROM persons WHERE isDeleted = 0")
    fun getAllWithFlow(): Flow<List<PersonEntity>>

    @Query("SELECT * FROM persons WHERE personId = :personId AND isDeleted = 0")
    suspend fun getById(personId: String): PersonEntity?

    @Query("SELECT * FROM persons WHERE personId = :personId")
    suspend fun getByIdIncludeDeleted(personId: String): PersonEntity?

    @Query("SELECT * FROM persons WHERE personType = :type AND isDeleted = 0")
    suspend fun getByPersonType(type: String): List<PersonEntity>

    @Query("SELECT * FROM persons WHERE personType = :type AND isDeleted = 0")
    fun getByPersonTypeWithFlow(type: String): Flow<List<PersonEntity>>

    @Delete
    suspend fun delete(person: PersonEntity)

    @Query("DELETE FROM persons")
    suspend fun deleteAll()

    @Query("UPDATE persons SET isDeleted = 1, needsSync = 1, updatedAt = :timestamp WHERE personId = :personId")
    suspend fun markAsDeleted(personId: String, timestamp: Long = System.currentTimeMillis()): Int

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


}
