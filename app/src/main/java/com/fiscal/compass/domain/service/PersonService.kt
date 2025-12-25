package com.fiscal.compass.domain.service

import com.fiscal.compass.domain.model.base.Person
import kotlinx.coroutines.flow.Flow

interface PersonService {
    /**
     * Adds a new person
     * @return UiState with success message or error
     */
    suspend fun addPerson(
        name: String,
        contact: String,
        personType: String
    ): Result<Any>

    /**
     * Updates an existing person
     * @return UiState with success message or error
     */
    suspend fun updatePerson(
        personId: Long,
        name: String,
        contact: String,
        personType: String
    ): Result<Any>

    /**
     * Deletes a person
     * @return UiState with success message or error
     */
    suspend fun deletePerson(person: Person): Result<Any>

    /**
     * Gets all persons with Flow
     */
    suspend fun getAllPersonsWithFlow(): Flow<List<Person>>

    /**
     * Gets all persons
     */
    suspend fun getAllPersons(): List<Person>

    /**
     * Gets persons by type with Flow
     */
    suspend fun getPersonByType(type: String): Flow<List<Person>>

    /**
     * Gets a person by ID
     */
    suspend fun getPersonById(id: Long): Person?
}
