package com.fiscal.compass.domain.service

import com.fiscal.compass.domain.model.base.Person
import com.fiscal.compass.domain.repository.PersonRepository
import com.fiscal.compass.domain.util.PersonType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PersonServiceImpl @Inject constructor(
    private val personRepository: PersonRepository
) : PersonService {

    override suspend fun addPerson(
        name: String,
        contact: String,
        personType: String
    ): Result<Any> {
        return try {
            val newPersonId = personRepository.getNextPersonId()
            val person = Person(
                personId = newPersonId,
                name = name,
                personType = PersonType.valueOf(personType).name,
                contact = contact
            )
            personRepository.addPerson(person)
            Result.success("Person added successfully.")
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }


    override suspend fun updatePerson(
        personId: String,
        name: String,
        contact: String,
        personType: String
    ): Result<Any> {
        return try {
            val person = Person(
                personId = personId,
                name = name,
                personType = personType,
                contact = contact
            )
            personRepository.updatePerson(person)
            Result.success("Person updated successfully.")
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override suspend fun updatePerson(updatedPerson: Person): Result<Any> {
        val currentPerson = personRepository.getPersonById(updatedPerson.personId) ?: return Result.failure(
            Exception("Person not found.")
        )
        val newPerson = currentPerson.copy(
            name = updatedPerson.name,
            contact = updatedPerson.contact,
            personType = updatedPerson.personType.uppercase()
        )
        val result = personRepository.updatePerson(newPerson)
        return if (result > 0) {
            Result.success("Person updated successfully.")
        } else {
            Result.failure(Exception("Failed to update person."))
        }
    }

    override suspend fun deletePerson(person: Person): Result<Any> {
        return try {
            personRepository.deletePerson(person)
            Result.success("Person deleted successfully.")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllPersonsWithFlow(): Flow<List<Person>> {
        return personRepository.getAllPersons()
    }

    override suspend fun getAllPersons(): List<Person> {
        return personRepository.getAllPersons().first()
    }

    override suspend fun getPersonById(personId: String): Person? {
        return personRepository.getPersonById(personId)
    }
}
