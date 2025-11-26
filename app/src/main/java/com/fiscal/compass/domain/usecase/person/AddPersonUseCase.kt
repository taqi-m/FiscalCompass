package com.fiscal.compass.domain.usecase.person

import com.fiscal.compass.domain.model.PersonType
import com.fiscal.compass.domain.model.base.Person
import com.fiscal.compass.domain.repository.PersonRepository
import com.fiscal.compass.presentation.screens.category.UiState
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