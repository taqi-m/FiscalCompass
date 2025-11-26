package com.fiscal.compass.domain.repository

import com.fiscal.compass.domain.model.base.Person

interface PersonRepository {
    suspend fun getPersonId(id: Long): String
    suspend fun addPerson(person: Person): Long
    suspend fun updatePerson(person: Person): Int
    suspend fun deletePerson(person: Person): Long
    suspend fun getAllPersons(): List<Person>
    suspend fun getPersonById(id: Long): Person?
}