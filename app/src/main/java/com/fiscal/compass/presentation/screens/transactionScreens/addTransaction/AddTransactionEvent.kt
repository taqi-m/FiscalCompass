package com.fiscal.compass.presentation.screens.transactionScreens.addTransaction

import com.fiscal.compass.presentation.model.TransactionType
import com.fiscal.compass.presentation.utils.AmountInputType
import java.util.Calendar

sealed class AddTransactionEvent {
    data class OnAmountChange(val amount: String, val inputType: AmountInputType = AmountInputType.TOTAL_AMOUNT) : AddTransactionEvent()
    data class OnAmountPaidChange(val amountPaid: String) : AddTransactionEvent()
    data class OnDescriptionChange(val description: String) : AddTransactionEvent()

    data class OnPersonSelected(val personId: Long?) : AddTransactionEvent()
    data class OnCategorySelected(val categoryId: Long) : AddTransactionEvent()

    data class OnTypeSelected(val selectedType: TransactionType) : AddTransactionEvent()

    object OnSaveClicked : AddTransactionEvent()
    object OnResetClicked : AddTransactionEvent()

    object OnUiReset : AddTransactionEvent()

    data class DateSelected(val selectedDate: Long) : AddTransactionEvent()
    data class TimeSelected(val selectedTime: Calendar) : AddTransactionEvent()

    // Navigation events for ItemSelectionScreen
    object NavigateToCategorySelection : AddTransactionEvent()
    object NavigateToPersonSelection : AddTransactionEvent()
    object ResetNavigation : AddTransactionEvent()

    // Update events from ItemSelectionScreen
    data class UpdateSelectedCategory(val categoryId: Long) : AddTransactionEvent()
    data class UpdateSelectedPerson(val personId: Long?) : AddTransactionEvent()
}
