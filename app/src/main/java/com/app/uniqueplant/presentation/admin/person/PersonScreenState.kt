package com.app.uniqueplant.presentation.admin.person

import com.app.uniqueplant.data.model.PersonType
import com.app.uniqueplant.presentation.admin.categories.UiState
import com.app.uniqueplant.presentation.model.PersonUi

data class PersonDialogState(
    val person: PersonUi? = null
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
    val persons: List<PersonUi> = emptyList(),
    val currentDialog: PersonDialog = PersonDialog.Hidden,
    val dialogState: PersonDialogState = PersonDialogState.Idle
)