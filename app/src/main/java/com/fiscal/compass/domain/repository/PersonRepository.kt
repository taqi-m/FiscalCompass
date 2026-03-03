package com.fiscal.compass.domain.repository

import com.fiscal.compass.domain.model.base.Person
import kotlinx.coroutines.flow.Flow

interface PersonRepository {
    suspend fun getPersonId(personId: String): String
    suspend fun addPerson(person: Person): Long
    suspend fun updatePerson(person: Person): Int
    suspend fun deletePerson(person: Person): Long
    suspend fun getAllPersons(): Flow<List<Person>>
    suspend fun getPersonById(personId: String): Person?
    fun getNextPersonId(): String
}