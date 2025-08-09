package com.app.uniqueplant.domain.usecase.person

import com.app.uniqueplant.data.datasource.local.dao.PersonDao

class GetAllPersonsUseCase(
    private val personDao: PersonDao
) {
    suspend fun getAllPersonsWithFlow() = personDao.getAllWithFlow()
    suspend fun getAllPersons() = personDao.getAll()

    suspend fun getPersonByType(type: String) = personDao.getByPersonTypeWithFlow(type)
}