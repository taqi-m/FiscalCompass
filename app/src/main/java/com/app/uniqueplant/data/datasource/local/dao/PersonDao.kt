package com.app.uniqueplant.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.uniqueplant.data.model.PersonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(person: PersonEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(persons: List<PersonEntity>)

    @Query("SELECT * FROM persons")
    suspend fun getAll(): List<PersonEntity>

    @Query("SELECT * FROM persons")
    fun getAllWithFlow(): Flow<List<PersonEntity>>

    @Query("SELECT * FROM persons WHERE personId = :id")
    suspend fun getById(id: String): PersonEntity?

    @Query("SELECT * FROM persons WHERE personType = :type")
    suspend fun getByPersonType(type: String): List<PersonEntity>
    @Query("SELECT * FROM persons WHERE personType = :type")
    fun getByPersonTypeWithFlow(type: String): Flow<List<PersonEntity>>

    @Update
    suspend fun update(person: PersonEntity)

    @Delete
    suspend fun delete(person: PersonEntity)

    @Query("DELETE FROM persons")
    suspend fun deleteAll()
}
