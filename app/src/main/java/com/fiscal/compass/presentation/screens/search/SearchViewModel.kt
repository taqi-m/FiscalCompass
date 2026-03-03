package com.fiscal.compass.presentation.screens.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiscal.compass.domain.service.CategoryService
import com.fiscal.compass.domain.service.PersonService
import com.fiscal.compass.domain.service.TransactionService
import com.fiscal.compass.domain.service.analytics.AnalyticsEvent
import com.fiscal.compass.domain.service.analytics.AnalyticsService
import com.fiscal.compass.domain.util.DateRange
import com.fiscal.compass.domain.util.SearchCriteria
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val transactionService: TransactionService,
    private val categoryService: CategoryService,
    private val personService: PersonService,
    private val analyticsService: AnalyticsService
) : ViewModel() {

    private val _state = MutableStateFlow(SearchResultsState())
    val state: StateFlow<SearchResultsState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadCategoriesAndPersons()
        fetchTransactions()
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.UpdateTempFilterType -> {
                val newCriteria = state.value.tempSearchCriteria.withTransactionType(event.type)
                _state.update { it.copy(tempSearchCriteria = newCriteria) }
            }

            is SearchEvent.TempStartDateSelected -> {
                val currentDateRange = state.value.tempSearchCriteria.dateRange
                val newDateRange = DateRange.from(event.startDate, currentDateRange?.endDate)
                val newCriteria = state.value.tempSearchCriteria.withDateRange(newDateRange)
                _state.update { it.copy(tempSearchCriteria = newCriteria) }
            }

            is SearchEvent.TempEndDateSelected -> {
                val currentDateRange = state.value.tempSearchCriteria.dateRange
                val newDateRange = DateRange.from(currentDateRange?.startDate, event.endDate)
                val newCriteria = state.value.tempSearchCriteria.withDateRange(newDateRange)
                _state.update { it.copy(tempSearchCriteria = newCriteria) }
            }

            is SearchEvent.UpdateSelectedCategories -> {
                Log.d("SearchViewModel", "Received category IDs: ${event.categoryIds}")
                val selectedCategories = state.value.allCategories.filter {
                    event.categoryIds.contains(it.categoryId)
                }
                val newCriteria = state.value.tempSearchCriteria.withCategories(selectedCategories)
                Log.d("SearchViewModel", "New category criteria: ${newCriteria.categories}")
                _state.update { it.copy(tempSearchCriteria = newCriteria) }
                Log.d("SearchViewModel", "State updated - tempSearchCriteria hash: ${_state.value.tempSearchCriteria.hashCode()}")
            }

            is SearchEvent.UpdateSelectedPersons -> {
                Log.d("SearchViewModel", "Received person IDs: ${event.personIds}")
                val selectedPersons = state.value.allPersons.filter {
                    event.personIds.contains(it.personId)
                }
                val newCriteria = state.value.tempSearchCriteria.withPersons(selectedPersons)
                Log.d("SearchViewModel", "New person criteria: ${newCriteria.persons}")
                _state.update { it.copy(tempSearchCriteria = newCriteria) }
                Log.d("SearchViewModel", "State updated - tempSearchCriteria hash: ${_state.value.tempSearchCriteria.hashCode()}")
            }

            SearchEvent.ApplyFilters -> {
                // Log analytics for filters applied
                val criteria = state.value.tempSearchCriteria
                analyticsService.logEvent(
                    AnalyticsEvent.SearchFiltersApplied(
                        hasTypeFilter = criteria.transactionType != null,
                        hasDateFilter = criteria.dateRange != null,
                        hasCategoryFilter = !criteria.categories.isNullOrEmpty(),
                        hasPersonFilter = !criteria.persons.isNullOrEmpty(),
                        categoryCount = criteria.categories?.size ?: 0,
                        personCount = criteria.persons?.size ?: 0
                    )
                )
                // Commit temp criteria to actual search criteria (already immutable, just copy reference)
                _state.update {
                    it.copy(searchCriteria = it.tempSearchCriteria)
                }
                fetchTransactions()
            }

            SearchEvent.ClearFilters -> {
                analyticsService.logEvent(AnalyticsEvent.SearchFiltersCleared)
                val emptyCriteria = SearchCriteria()
                _state.update {
                    it.copy(
                        searchCriteria = emptyCriteria,
                        tempSearchCriteria = emptyCriteria
                    )
                }
                fetchTransactions()
            }

            SearchEvent.ResetTempFilters -> {
                Log.d("SearchViewModel", "ResetTempFilters - copying searchCriteria to tempSearchCriteria")
                // Reset temp criteria to match current search criteria (already immutable, just copy reference)
                _state.update {
                    it.copy(tempSearchCriteria = it.searchCriteria)
                }
            }

            is SearchEvent.UpdateTempDateRange -> {
                val newCriteria = state.value.tempSearchCriteria.withDateRange(event.dateRange)
                _state.update { it.copy(tempSearchCriteria = newCriteria) }
            }
        }
    }

    private fun fetchTransactions() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(displayState = SearchResultsDisplayState.Loading) }
            try {
                val criteria = state.value.searchCriteria
                transactionService.searchTransactions(criteria).collect { results ->
                    _state.update {
                        it.copy(displayState = SearchResultsDisplayState.Content(searchResults = results))
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(displayState = SearchResultsDisplayState.Error(
                        message = e.message ?: "An unexpected error occurred"
                    ))
                }
            }
        }
    }

    private fun loadCategoriesAndPersons() {
        viewModelScope.launch {
            try {
                val categories = categoryService.getAllCategories()
                val persons = personService.getAllPersons()
                _state.update {
                    it.copy(
                        allCategories = categories,
                        allPersons = persons
                    )
                }
            } catch (e: Exception) {
                // Handle error if needed - could update a separate loading state
            }
        }
    }
}
