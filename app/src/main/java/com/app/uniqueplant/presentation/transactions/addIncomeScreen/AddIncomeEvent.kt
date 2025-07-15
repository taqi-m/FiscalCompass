package com.app.uniqueplant.presentation.transactions.addIncomeScreen

import java.util.Date

sealed class AddIncomeEvent {
    data class OnAmountChange(val amount: String) : AddIncomeEvent()
    data class OnDescriptionChange(val description: String) : AddIncomeEvent()
    data class OnDateChange(val date: Date) : AddIncomeEvent()
    data class OnCategorySelected(val categoryId: Long) : AddIncomeEvent()
    data class OnAccountSelected(val accountId: Long) : AddIncomeEvent()
    data class DateSelected(val selectedDate: Long?) : AddIncomeEvent()

    object OnSaveClicked : AddIncomeEvent()
    object OnResetClicked : AddIncomeEvent()
    object OnDialogDismiss : AddIncomeEvent()
}
