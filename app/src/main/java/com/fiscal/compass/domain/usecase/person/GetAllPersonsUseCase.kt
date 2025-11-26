package com.fiscal.compass.domain.usecase.person

import com.fiscal.compass.data.mappers.toDomain
import com.fiscal.compass.data.local.dao.PersonDao
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllPersonsUseCase @Inject constructor(
    private val personDao: PersonDao
) {
    suspend fun getAllPersonsWithFlow() = personDao.getAllWithFlow().map { personList -> personList.map { it.toDomain() } }
    suspend fun getAllPersons() = personDao.getAll().map { it.toDomain() }

    suspend fun getPersonByType(type: String) = personDao.getByPersonTypeWithFlow(type).map { list -> list.map { it.toDomain() } }
}