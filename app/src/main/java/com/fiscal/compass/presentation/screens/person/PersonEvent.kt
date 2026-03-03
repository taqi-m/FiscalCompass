package com.fiscal.compass.presentation.screens.person

import com.fiscal.compass.domain.model.base.Person

sealed class PersonDialogToggle {
    data class Delete(val person: Person) : PersonDialogToggle()
    object Hidden : PersonDialogToggle()
}

sealed class PersonDialogSubmit {
    object Delete : PersonDialogSubmit()
}

sealed class PersonEvent {
    object OnUiReset : PersonEvent()
    data class OnPersonDialogToggle(val event: PersonDialogToggle) : PersonEvent()
    data class OnPersonDialogSubmit(val event: PersonDialogSubmit) : PersonEvent()
    data class OnFilterTypeSelected(val selectedType: String) : PersonEvent()
}