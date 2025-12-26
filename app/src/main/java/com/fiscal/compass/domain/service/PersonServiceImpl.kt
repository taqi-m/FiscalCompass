package com.fiscal.compass.domain.service

import com.fiscal.compass.data.local.dao.PersonDao
import com.fiscal.compass.data.mappers.toDomain
import com.fiscal.compass.data.mappers.toEntity
import com.fiscal.compass.domain.model.base.Person
import com.fiscal.compass.domain.repository.PersonRepository
import com.fiscal.compass.domain.util.PersonType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PersonServiceImpl @Inject constructor(
    private val personRepository: PersonRepository,
    private val personDao: PersonDao
) : PersonService {

    override suspend fun addPerson(
        name: String,
        contact: String,
        personType: String
    ): Result<Any> {
        return try {
            val person = Person(
                personId = 0,
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
        personId: Long,
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
        val currentPerson = personDao.getById(updatedPerson.personId)
        if (currentPerson == null) {
            return Result.failure(Exception("Person not found."))
        }
        val newPerson = currentPerson.copy(
            name = updatedPerson.name,
            contact = updatedPerson.contact,
            personType = PersonType.valueOf(updatedPerson.personType.uppercase())
        )
        val result = personDao.update(newPerson)
        return if (result > 0) {
            Result.success("Person updated successfully.")
        } else {
            Result.failure(Exception("Failed to update person."))
        }
    }

    override suspend fun deletePerson(person: Person): Result<Any> {
        return try {
            personDao.delete(person.toEntity())
            Result.success("Person deleted successfully.")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllPersonsWithFlow(): Flow<List<Person>> {
        return personDao.getAllWithFlow().map { personList ->
            personList.map { it.toDomain() }
        }
    }

    override suspend fun getAllPersons(): List<Person> {
        return personDao.getAll().map { it.toDomain() }
    }

    override suspend fun getPersonByType(type: String): Flow<List<Person>> {
        return personDao.getByPersonTypeWithFlow(type).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getPersonById(id: Long): Person? {
        return personRepository.getPersonById(id)
    }
}
