package com.fiscal.compass.domain.usecase.person

import com.fiscal.compass.domain.model.base.Person
import com.fiscal.compass.domain.repository.PersonRepository
import javax.inject.Inject

class EditPersonUC @Inject constructor(
    private val personRepository: PersonRepository
) {
    suspend operator fun invoke(personId: Long, name: String, contact: String, personType: String) {
        val person = Person(personId, name, personType, contact)
        personRepository.updatePerson(person)
    }
}