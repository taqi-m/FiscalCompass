package com.app.uniqueplant.presentation.admin.person

import com.app.uniqueplant.data.model.PersonEntity

sealed class PersonDialogToggle {
    object Add : PersonDialogToggle()
    data class Edit(val person: PersonEntity) : PersonDialogToggle()
    data class Delete(val person: PersonEntity) : PersonDialogToggle()
    object Hidden : PersonDialogToggle()
}
sealed class PersonDialogSubmit {
    data class Add(val name: String, val personType: String) : PersonDialogSubmit()
    data class Edit(val person: PersonEntity) : PersonDialogSubmit()
    object Delete : PersonDialogSubmit()
}

sealed class PersonEvent {
    object OnUiReset : PersonEvent()
    data class OnPersonDialogToggle(val event: PersonDialogToggle) : PersonEvent()
    data class OnPersonDialogSubmit(val event: PersonDialogSubmit) : PersonEvent()
    data class OnPersonDialogStateChange(val state: PersonDialogState) : PersonEvent()
    data class OnFilterTypeSelected(val selectedType: String) : PersonEvent()
}