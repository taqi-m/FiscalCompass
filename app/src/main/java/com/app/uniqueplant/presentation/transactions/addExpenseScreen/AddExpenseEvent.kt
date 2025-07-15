package com.app.uniqueplant.presentation.transactions.addExpenseScreen

import java.util.Date

sealed class AddExpenseEvent {
    data class OnAmountChange(val amount: String) : AddExpenseEvent()
    data class OnDescriptionChange(val description: String) : AddExpenseEvent()
    data class OnDateChange(val date: Date) : AddExpenseEvent()
    data class OnCategorySelected(val categoryId: Long) : AddExpenseEvent()
    data class OnAccountSelected(val accountId: Long) : AddExpenseEvent()
    data class DateSelected(val selectedDate: Long?) : AddExpenseEvent()

    object OnSaveClicked : AddExpenseEvent()
    object OnResetClicked : AddExpenseEvent()
    object OnDialogDismiss : AddExpenseEvent()
}
