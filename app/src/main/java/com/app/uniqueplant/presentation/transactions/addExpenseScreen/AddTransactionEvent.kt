package com.app.uniqueplant.presentation.transactions.addExpenseScreen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import com.app.uniqueplant.domain.model.TransactionType
import java.util.Date

sealed class AddTransactionEvent {
    data class OnAmountChange(val amount: String) : AddTransactionEvent()
    data class OnDescriptionChange(val description: String) : AddTransactionEvent()
    data class OnDateChange(val date: Date) : AddTransactionEvent()
    data class OnCategorySelected(val categoryId: Long) : AddTransactionEvent()
    data class OnAccountSelected(val accountId: Long) : AddTransactionEvent()
    data class DateSelected(val selectedDate: Long?) : AddTransactionEvent()
    data class OnTimeSelected @OptIn(ExperimentalMaterial3Api::class) constructor(val selectedTime: TimePickerState?) : AddTransactionEvent()

    data class OnTypeSelected(val selectedType: TransactionType) : AddTransactionEvent()

    object OnSaveClicked : AddTransactionEvent()
    object OnResetClicked : AddTransactionEvent()
    object OnDateDialogToggle : AddTransactionEvent()
    object OnTimeDialogToggle : AddTransactionEvent()

    object OnSuccessHandled : AddTransactionEvent()
}
