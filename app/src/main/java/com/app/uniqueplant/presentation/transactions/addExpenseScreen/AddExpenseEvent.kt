package com.app.uniqueplant.presentation.transactions.addExpenseScreen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import java.util.Date

sealed class AddExpenseEvent {
    data class OnAmountChange(val amount: String) : AddExpenseEvent()
    data class OnDescriptionChange(val description: String) : AddExpenseEvent()
    data class OnDateChange(val date: Date) : AddExpenseEvent()
    data class OnCategorySelected(val categoryId: Long) : AddExpenseEvent()
    data class OnAccountSelected(val accountId: Long) : AddExpenseEvent()
    data class DateSelected(val selectedDate: Long?) : AddExpenseEvent()
    data class OnTimeSelected @OptIn(ExperimentalMaterial3Api::class) constructor(val selectedTime: TimePickerState?) : AddExpenseEvent()

    object OnSaveClicked : AddExpenseEvent()
    object OnResetClicked : AddExpenseEvent()
    object OnDateDialogToggle : AddExpenseEvent()
    object OnTimeDialogToggle : AddExpenseEvent()

    object OnSuccessHandled : AddExpenseEvent()
}
