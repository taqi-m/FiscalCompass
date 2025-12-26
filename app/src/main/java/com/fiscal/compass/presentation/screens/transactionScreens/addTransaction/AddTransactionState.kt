package com.fiscal.compass.presentation.screens.transactionScreens.addTransaction

import com.fiscal.compass.domain.model.Transaction
import com.fiscal.compass.domain.model.base.Category
import com.fiscal.compass.domain.model.base.Person
import com.fiscal.compass.presentation.screens.category.UiState

data class AddTransactionState(
    val uiState: UiState = UiState.Idle,
    val transaction: Transaction = Transaction.default(),
    val allCategories: List<Category> = emptyList(),
    val allPersons: List<Person> = emptyList(),
    val navigateToCategorySelection: Boolean = false,
    val navigateToPersonSelection: Boolean = false,
    val navigateToAmountScreen: Boolean = false
)