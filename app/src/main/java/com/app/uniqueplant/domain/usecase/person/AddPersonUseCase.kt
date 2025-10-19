package com.app.uniqueplant.domain.usecase.person

import com.app.uniqueplant.domain.model.PersonType
import com.app.uniqueplant.domain.model.base.Person
import com.app.uniqueplant.domain.repository.PersonRepository
import com.app.uniqueplant.presentation.screens.category.UiState
import javax.inject.Inject


class AddPersonUseCase @Inject constructor(
    private val personRepository: PersonRepository
) {
    suspend operator fun invoke(name: String,contact: String,  personType: String): UiState {
        try{
            val person = Person(0, name, PersonType.valueOf(personType).name, contact)
            personRepository.addPerson(person)
            return UiState.Success("Person added successfully.")
        }catch (exception: Exception) {
            return UiState.Error(exception.message ?: "An error occurred while adding the person.")
        }
    }
}