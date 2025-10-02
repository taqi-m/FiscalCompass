package com.app.uniqueplant.domain.usecase.person

import com.app.uniqueplant.data.mappers.toDomain
import com.app.uniqueplant.data.local.dao.PersonDao
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllPersonsUseCase @Inject constructor(
    private val personDao: PersonDao
) {
    suspend fun getAllPersonsWithFlow() = personDao.getAllWithFlow().map { personList -> personList.map { it.toDomain() } }
    suspend fun getAllPersons() = personDao.getAll().map { it.toDomain() }

    suspend fun getPersonByType(type: String) = personDao.getByPersonTypeWithFlow(type).map { list -> list.map { it.toDomain() } }
}