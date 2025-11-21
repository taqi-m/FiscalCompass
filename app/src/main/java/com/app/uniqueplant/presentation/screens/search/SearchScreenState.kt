package com.app.uniqueplant.presentation.screens.search

import com.app.uniqueplant.domain.model.base.Category
import com.app.uniqueplant.domain.model.base.Person
import com.app.uniqueplant.presentation.model.TransactionUi
import com.app.uniqueplant.presentation.screens.category.UiState
import java.util.Date

sealed class SearchScreenDialog {
    object Hidden : SearchScreenDialog()
    object DatePicker : SearchScreenDialog()
    object TimePicker : SearchScreenDialog()
}


data class SearchScreenState(
    val uiState: UiState = UiState.Idle,
    val showFilterDialog: Boolean = false,
    val searchResults: Map<Date, List<TransactionUi>> = emptyMap(),
    val currentDialog: SearchScreenDialog = SearchScreenDialog.Hidden,
    val filterType: String? = null,
    val filterCategories: MutableList<Long>? = mutableListOf(),
    val filterPersons: MutableList<Long>? = mutableListOf(),
    val filterStartDate: Long? = null,
    val filterEndDate: Long? = null,
    val allCategories: List<Category> = emptyList(),
    val allPersons: List<Person> = emptyList(),
    val navigateToCategorySelection: Boolean = false,
    val navigateToPersonSelection: Boolean = false
)
