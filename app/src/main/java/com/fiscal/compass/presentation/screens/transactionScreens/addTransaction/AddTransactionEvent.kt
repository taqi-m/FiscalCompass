package com.fiscal.compass.presentation.screens.transactionScreens.addTransaction

import com.fiscal.compass.domain.util.TransactionType

sealed class AddTransactionEvent {
    data class OnDescriptionChange(val description: String) : AddTransactionEvent()
    data class OnTypeSelected(val selectedType: TransactionType) : AddTransactionEvent()
    object OnResetClicked : AddTransactionEvent()
    object OnUiReset : AddTransactionEvent()
    data class DateSelected(val selectedDate: Long) : AddTransactionEvent()
    data class TimeSelected(val selectedTime: Long) : AddTransactionEvent()

    // Update events from inline ItemSelectionScreen composables
    data class UpdateSelectedCategory(val categoryId: String) : AddTransactionEvent()
    data class UpdateSelectedPerson(val personId: String?) : AddTransactionEvent()

    // Sets editMode on state before navigating to Amount screen
    data class SetEditMode(val editMode: Boolean) : AddTransactionEvent()
}
