package com.fiscal.compass.data.repositories

import com.fiscal.compass.data.local.dao.PersonDao
import com.fiscal.compass.data.managers.AutoSyncManager
import com.fiscal.compass.data.managers.SyncType
import com.fiscal.compass.data.mappers.toEntity
import com.fiscal.compass.domain.model.base.Person
import com.fiscal.compass.domain.repository.PersonRepository
import javax.inject.Inject

class PersonRepositoryImpl @Inject constructor(
    private val personDao: PersonDao,
    private val autoSyncManager: AutoSyncManager
) : PersonRepository {
    override suspend fun getPersonId(id: Long): String {
        TODO("Not yet implemented")
    }

    override suspend fun addPerson(person: Person): Long {
        val personEntity = person.toEntity()
        val dbResult = personDao.insert(personEntity)
        autoSyncManager.triggerSync(SyncType.PERSONS)
        return dbResult
    }

    override suspend fun updatePerson(person: Person): Int {
        val currentTime = System.currentTimeMillis()
        val existingPerson = personDao.getPersonById(person.personId)
        val personEntity = person.toEntity().copy(
            needsSync = true,
            isSynced = false,
            createdAt = existingPerson?.createdAt ?: currentTime,
            updatedAt = currentTime,
            firestoreId = existingPerson?.firestoreId
        )

        val dbResult = personDao.update(personEntity)
        autoSyncManager.triggerSync(SyncType.PERSONS)
        return dbResult
    }

    override suspend fun deletePerson(person: Person): Long {
        TODO("Not yet implemented")
    }

    override suspend fun getAllPersons(): List<Person> {
        TODO("Not yet implemented")
    }

    override suspend fun getPersonById(id: Long): Person? {
        TODO("Not yet implemented")
    }
}