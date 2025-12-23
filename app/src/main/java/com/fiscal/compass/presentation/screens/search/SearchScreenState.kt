package com.fiscal.compass.presentation.screens.search

import com.fiscal.compass.domain.model.Transaction
import com.fiscal.compass.domain.model.base.Category
import com.fiscal.compass.domain.model.base.Person
import com.fiscal.compass.domain.util.SearchCriteria
import com.fiscal.compass.presentation.model.TransactionUi
import com.fiscal.compass.presentation.screens.category.UiState
import java.util.Date

data class SearchScreenState(
    val uiState: UiState = UiState.Idle,
    val showFilterDialog: Boolean = false,
    val searchResults: Map<Date, List<Transaction>> = emptyMap(),
    val searchCriteria: SearchCriteria = SearchCriteria(),
    val allCategories: List<Category> = emptyList(),
    val allPersons: List<Person> = emptyList(),
    val navigateToCategorySelection: Boolean = false,
    val navigateToPersonSelection: Boolean = false
)
