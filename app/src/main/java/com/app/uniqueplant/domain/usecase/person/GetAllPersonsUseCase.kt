package com.app.uniqueplant.domain.usecase.person

import com.app.uniqueplant.data.datasource.local.dao.PersonDao
import com.app.uniqueplant.data.mapper.toDomain
import kotlinx.coroutines.flow.map

class GetAllPersonsUseCase(
    private val personDao: PersonDao
) {
    suspend fun getAllPersonsWithFlow() = personDao.getAllWithFlow().map { personList -> personList.map { it.toDomain() } }
    suspend fun getAllPersons() = personDao.getAll()

    suspend fun getPersonByType(type: String) = personDao.getByPersonTypeWithFlow(type).map { list -> list.map { it.toDomain() } }
}