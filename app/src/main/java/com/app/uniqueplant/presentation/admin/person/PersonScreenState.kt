package com.app.uniqueplant.presentation.admin.person

import com.app.uniqueplant.data.model.Person
import com.app.uniqueplant.data.model.PersonTypes
import com.app.uniqueplant.presentation.admin.categories.UiState

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
    val selectedType: String = PersonTypes.CUSTOMER.name,
    val persons: List<Person> = emptyList(),
    val currentDialog: PersonDialog = PersonDialog.Hidden,
    val dialogState: PersonDialogState = PersonDialogState.Idle
)