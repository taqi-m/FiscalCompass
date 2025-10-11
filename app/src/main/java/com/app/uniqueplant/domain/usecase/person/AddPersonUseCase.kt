package com.app.uniqueplant.domain.usecase.person

import com.app.uniqueplant.data.local.model.PersonType
import com.app.uniqueplant.domain.model.dataModels.Person
import com.app.uniqueplant.domain.repository.PersonRepository
import com.app.uniqueplant.presentation.screens.categories.UiState
import javax.inject.Inject


/*
* TODO: Implement PersonRepository to handle data operations
*/

class AddPersonUseCase @Inject constructor(
    private val personRepository: PersonRepository
) {
    suspend operator fun invoke(name: String, personType: String): UiState {
        try{
            val person = Person(
                personId = 0,
                name = name,
                personType = PersonType.valueOf(personType).name
            )
            personRepository.addPerson(person)
            return UiState.Success("Person added successfully.")
        }catch (exception: Exception) {
            return UiState.Error(exception.message ?: "An error occurred while adding the person.")
        }
    }
}