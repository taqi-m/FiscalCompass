package com.app.uniqueplant.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.uniqueplant.domain.usecase.transaction.SearchTransactionUC
import com.app.uniqueplant.presentation.mappers.toUi
import com.app.uniqueplant.presentation.screens.categories.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchTransactionUC: SearchTransactionUC
) : ViewModel() {

    private val _state = MutableStateFlow(SearchScreenState())
    val state: StateFlow<SearchScreenState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        fetchTransactions()
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            SearchEvent.OnFilterIconClicked -> {
                updateState { copy(showFilterDialog = true) }
            }
            SearchEvent.OnDismissFilterDialog -> {
                updateState { copy(showFilterDialog = false) }
            }
            is SearchEvent.UpdateFilterType -> {
                updateState { copy(filterType = event.type) }
                fetchTransactions()
            }
            is SearchEvent.SubmitFilterCategory -> {
                val currentCategories = state.value.filterCategories ?: mutableListOf()
                if (currentCategories.contains(event.categoryId)) {
                    currentCategories.remove(event.categoryId)
                } else {
                    currentCategories.add(event.categoryId)
                }
                updateState { copy(filterCategories = currentCategories) }
            }
            is SearchEvent.SubmitFilterPerson -> {
                val currentPersons = state.value.filterPersons ?: mutableListOf()
                if (currentPersons.contains(event.personId)) {
                    currentPersons.remove(event.personId)
                } else {
                    currentPersons.add(event.personId)
                }
                updateState { copy(filterPersons = currentPersons) }
            }

            is SearchEvent.UpdateFilterDateRange -> {
                updateState { copy(filterStartDate = event.startDate, filterEndDate = event.endDate) }
            }
            SearchEvent.ApplyFilters -> {
                updateState { copy(showFilterDialog = false) }
                fetchTransactions()
            }
            SearchEvent.ClearFilters -> {
                updateState {
                    copy(
                        filterType = null,
                        filterCategories = mutableListOf(),
                        filterPersons = mutableListOf(),
                        filterStartDate = null,
                        filterEndDate = null
                    )
                }
                fetchTransactions()
            }
        }
    }

    private fun fetchTransactions() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            updateState { copy(uiState = UiState.Loading) }
            try {
                val results = searchTransactionUC(
                    personIds = state.value.filterPersons,
                    categoryIds = state.value.filterCategories,
                    startDate = state.value.filterStartDate,
                    endDate = state.value.filterEndDate,
                    filterType = state.value.filterType
                ).mapValues {
                    it.value.map { transaction -> transaction.toUi() }
                }
                updateState { copy(uiState = UiState.Idle, searchResults = results) }
            } catch (e: Exception) {
                updateState { copy(uiState = UiState.Error(e.message ?: "An unexpected error occurred")) }
            }
        }
    }

    private fun updateState(update: SearchScreenState.() -> SearchScreenState) {
        _state.value = _state.value.update()
    }
}
