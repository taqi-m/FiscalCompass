package com.app.uniqueplant.data.repositories

import com.app.uniqueplant.data.local.dao.PersonDao
import com.app.uniqueplant.data.managers.AutoSyncManager
import com.app.uniqueplant.data.managers.SyncType
import com.app.uniqueplant.data.mappers.toEntity
import com.app.uniqueplant.domain.model.base.Person
import com.app.uniqueplant.domain.repository.PersonRepository
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