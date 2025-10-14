package com.app.uniqueplant.presentation.screens.search

import com.app.uniqueplant.domain.model.dataModels.Category
import com.app.uniqueplant.domain.model.dataModels.Person
import com.app.uniqueplant.presentation.model.TransactionUi
import com.app.uniqueplant.presentation.screens.categories.UiState
import java.util.Date

data class SearchScreenState(
    val uiState: UiState = UiState.Idle,
    val showFilterDialog: Boolean = false,
    val searchResults: Map<Date, List<TransactionUi>> = emptyMap(),
    val filterType: String? = null,
    val filterCategories: MutableList<Long>? = mutableListOf(),
    val filterPersons: MutableList<Long>? = mutableListOf(),
    val filterStartDate: Long? = null,
    val filterEndDate: Long? = null,
    val allCategories: List<Category> = emptyList(),
    val allPersons: List<Person> = emptyList(),
    val showCategoryFilterDialog: Boolean = false,
    val showPersonFilterDialog: Boolean = false
)
