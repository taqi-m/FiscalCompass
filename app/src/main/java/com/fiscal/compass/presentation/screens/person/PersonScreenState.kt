package com.fiscal.compass.presentation.screens.person

import com.fiscal.compass.domain.model.PersonType
import com.fiscal.compass.domain.model.base.Person
import com.fiscal.compass.presentation.screens.category.UiState

data class PersonDialogState(
    val person: Person? = null
) {
    companion object {
        val Idle = PersonDialogState()
    }
}

sealed class PersonDialog {
    object Hidden : PersonDialog()
    object AddPerson : PersonDialog()
    object EditPerson : PersonDialog()
    object DeletePerson : PersonDialog()
}

data class PersonScreenState(
    val uiState: UiState = UiState.Idle,
    val selectedType: String = PersonType.CUSTOMER.name,
    val persons: List<Person> = emptyList(),
    val currentDialog: PersonDialog = PersonDialog.Hidden,
    val dialogState: PersonDialogState = PersonDialogState.Idle,
    val canAdd: Boolean = false,
    val canEdit: Boolean = false,
    val canDelete: Boolean = false
)