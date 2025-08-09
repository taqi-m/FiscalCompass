package com.app.uniqueplant.domain.usecase.person

import com.app.uniqueplant.data.datasource.local.dao.PersonDao
import com.app.uniqueplant.data.model.Person
import com.app.uniqueplant.presentation.admin.categories.UiState

class AddPersonUseCase(
    private val personDao: PersonDao
) {
    suspend operator fun invoke(name: String, personType: String): UiState {
        try{
            val person = Person(
                name = name,
                personType = personType
            )
            personDao.insert(person)
            return UiState.Success("Person added successfully.")
        }catch (exception: Exception) {
            return UiState.Error(exception.message ?: "An error occurred while adding the person.")
        }
    }
}