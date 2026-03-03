package com.fiscal.compass.presentation.screens.person.addperson

sealed class AddPersonEvent {
    data class UpdateName(val name: String) : AddPersonEvent()
    data class UpdateContact(val contact: String) : AddPersonEvent()
    data class UpdateType(val type: String) : AddPersonEvent()
    data object Submit : AddPersonEvent()
    data object ClearError : AddPersonEvent()
}

