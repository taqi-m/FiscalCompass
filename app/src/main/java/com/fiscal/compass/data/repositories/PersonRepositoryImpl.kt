package com.fiscal.compass.data.repositories

import com.fiscal.compass.data.local.dao.PersonDao
import com.fiscal.compass.data.mappers.toDomain
import com.fiscal.compass.data.mappers.toEntity
import com.fiscal.compass.data.remote.RemoteUtil
import com.fiscal.compass.domain.model.base.Person
import com.fiscal.compass.domain.model.sync.SyncType
import com.fiscal.compass.domain.repository.PersonRepository
import com.fiscal.compass.domain.sync.AutoSyncManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PersonRepositoryImpl @Inject constructor(
    private val personDao: PersonDao,
    private val autoSyncManager: AutoSyncManager
) : PersonRepository {
    override suspend fun getPersonId(personId: String): String {
        return personId // Return the same ID as it's already a String
    }

    override suspend fun getAllPersons(): Flow<List<Person>> {
        return personDao.getAllWithFlow().map { it.map { personEntity -> personEntity.toDomain() } }
    }

    override suspend fun getPersonById(personId: String): Person? {
        return personDao.getPersonById(personId)?.toDomain()
    }

    override fun getNextPersonId(): String {
        val newPersonId = RemoteUtil.generatePersonId()
        val validPersonId = RemoteUtil.ensureValidPersonId(newPersonId)
        return validPersonId
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
            updatedAt = currentTime
        )

        val dbResult = personDao.update(personEntity)
        autoSyncManager.triggerSync(SyncType.PERSONS)
        return dbResult
    }

    override suspend fun deletePerson(person: Person): Long {
        val result = personDao.markAsDeleted(person.personId)
        autoSyncManager.triggerSync(SyncType.PERSONS)
        return result.toLong()
    }

}