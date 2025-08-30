package com.app.uniqueplant.domain.usecase.person

import com.app.uniqueplant.data.datasource.local.dao.PersonDao
import com.app.uniqueplant.data.model.PersonEntity
import com.app.uniqueplant.data.model.PersonType
import com.app.uniqueplant.presentation.screens.categories.UiState
import javax.inject.Inject


/*
* TODO: Implement PersonRepository to handle data operations
*/

class AddPersonUseCase @Inject constructor(
    private val personDao: PersonDao
) {
    suspend operator fun invoke(name: String, personType: String): UiState {
        try{
            val person = PersonEntity(
                name = name,
                personType = PersonType.fromString(personType)
            )
            personDao.insert(person)
            return UiState.Success("Person added successfully.")
        }catch (exception: Exception) {
            return UiState.Error(exception.message ?: "An error occurred while adding the person.")
        }
    }
}