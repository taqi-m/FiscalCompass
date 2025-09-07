package com.app.uniqueplant.presentation.screens.search

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
    val filterStartDate: Long? = null, // Using Long for timestamp, can be adjusted
    val filterEndDate: Long? = null,   // Using Long for timestamp, can be adjusted
)
