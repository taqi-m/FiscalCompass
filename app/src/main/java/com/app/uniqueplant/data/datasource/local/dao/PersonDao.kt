package com.app.uniqueplant.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.uniqueplant.data.model.Person
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(person: Person)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(persons: List<Person>)

    @Query("SELECT * FROM persons")
    suspend fun getAll(): List<Person>

    @Query("SELECT * FROM persons")
    fun getAllWithFlow(): Flow<List<Person>>

    @Query("SELECT * FROM persons WHERE personId = :id")
    suspend fun getById(id: String): Person?

    @Query("SELECT * FROM persons WHERE personType = :type")
    suspend fun getByPersonType(type: String): List<Person>
    @Query("SELECT * FROM persons WHERE personType = :type")
    fun getByPersonTypeWithFlow(type: String): Flow<List<Person>>

    @Update
    suspend fun update(person: Person)

    @Delete
    suspend fun delete(person: Person)

    @Query("DELETE FROM persons")
    suspend fun deleteAll()
}
