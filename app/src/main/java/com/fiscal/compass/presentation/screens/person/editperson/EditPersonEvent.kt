package com.fiscal.compass.presentation.screens.person.editperson

import com.fiscal.compass.domain.model.base.Person

sealed class EditPersonEvent {
    data class LoadPerson(val person: Person) : EditPersonEvent()
    data class UpdateName(val name: String) : EditPersonEvent()
    data class UpdateContact(val contact: String) : EditPersonEvent()
    data class UpdateType(val type: String) : EditPersonEvent()
    data object Submit : EditPersonEvent()
    data object ClearError : EditPersonEvent()
}

