package com.app.uniqueplant.presentation.screens.transactionScreens.addTransaction

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import com.app.uniqueplant.presentation.model.TransactionType
import java.util.Date

sealed class AddTransactionDialogToggle {
    object DatePicker : AddTransactionDialogToggle()
    object TimePicker : AddTransactionDialogToggle()
    object Hidden : AddTransactionDialogToggle()
}

sealed class AddTransactionDialogSubmit {
    data class DateSelected(val selectedDate: Long?) : AddTransactionDialogSubmit()
    data class TimeSelected @OptIn(ExperimentalMaterial3Api::class) constructor(val selectedTime: TimePickerState?) : AddTransactionDialogSubmit()
    object Hidden : AddTransactionDialogSubmit()
}

sealed class AddTransactionEvent {
    data class OnAmountChange(val amount: String) : AddTransactionEvent()
    data class OnDescriptionChange(val description: String) : AddTransactionEvent()
    data class OnDateChange(val date: Date) : AddTransactionEvent()

    data class OnPersonSelected(val personId: Long?) : AddTransactionEvent()
    data class OnCategorySelected(val categoryId: Long) : AddTransactionEvent()

    data class OnSubCategorySelected(val subCategoryId: Long?) : AddTransactionEvent()

    data class OnTypeSelected(val selectedType: TransactionType) : AddTransactionEvent()

    object OnSaveClicked : AddTransactionEvent()
    object OnResetClicked : AddTransactionEvent()

    object OnUiReset : AddTransactionEvent()
    data class OnAddTransactionDialogToggle(val event: AddTransactionDialog) : AddTransactionEvent()
    data class OnAddTransactionDialogSubmit(val event: AddTransactionDialogSubmit) : AddTransactionEvent()
}
