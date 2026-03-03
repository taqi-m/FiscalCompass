package com.fiscal.compass.presentation.screens.person

import com.fiscal.compass.domain.util.PersonType
import com.fiscal.compass.domain.model.base.Person

// Operation state for tracking UI operations
sealed interface PersonUiState {
    data object Idle : PersonUiState
    data object Loading : PersonUiState
    data class Success(val message: String) : PersonUiState
    data class Error(val message: String) : PersonUiState
}

// Shared data that remains visible across all states
data class PersonState(
    val selectedType: String = PersonType.CUSTOMER.name,
    val uiState: PersonUiState = PersonUiState.Idle,
    val dialogState: PersonDialogState = PersonDialogState.Hidden,
    val permissions: PersonPermissions = PersonPermissions(),
    val displayState: PersonDisplayState = PersonDisplayState.Loading
)

// Content data
data class PersonContentData(
    val persons: List<Person> = emptyList(),
    val filteredPersons: List<Person> = emptyList()
)

// Display state sealed interface
sealed interface PersonDisplayState {
    data object Loading : PersonDisplayState
    data class Error(val message: String) : PersonDisplayState
    data class Content(val data: PersonContentData) : PersonDisplayState
}

// Unified permissions
data class PersonPermissions(
    val canAdd: Boolean = false,
    val canEdit: Boolean = false,
    val canDelete: Boolean = false
)

// Unified dialog state
sealed class PersonDialogState {
    data object Hidden : PersonDialogState()
    data class DeletePerson(val person: Person) : PersonDialogState()
}

