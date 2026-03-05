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
import kotlinx.coroutines.CancellationException
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
                val criteria = state.value.tempSearchCriteria
                Log.d("SearchViewModel", "Applying filters: $criteria")

                // Log analytics for filters applied
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

                // Commit temp criteria to actual search criteria
                _state.update {
                    it.copy(searchCriteria = criteria)
                }

                Log.d("SearchViewModel", "Applied filters: ${state.value.searchCriteria}")

                // Call fetchTransactions directly without delay
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

                // Call fetchTransactions directly without delay
                fetchTransactions()
            }

            SearchEvent.ResetTempFilters -> {
                Log.d("SearchViewModel", "ResetTempFilters - copying searchCriteria to tempSearchCriteria")
                Log.d("SearchViewModel", "Current searchCriteria: ${state.value.searchCriteria}")

                // Reset temp criteria to match current search criteria
                _state.update {
                    it.copy(tempSearchCriteria = it.searchCriteria)
                }

                Log.d("SearchViewModel", "After reset tempSearchCriteria: ${state.value.tempSearchCriteria}")
            }

            is SearchEvent.UpdateTempDateRange -> {
                val newCriteria = state.value.tempSearchCriteria.withDateRange(event.dateRange)
                _state.update { it.copy(tempSearchCriteria = newCriteria) }
            }
        }
    }

    private fun fetchTransactions() {
        // Cancel previous job and start a new one
        searchJob?.cancel()

        searchJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val criteria = state.value.searchCriteria
                Log.d("SearchViewModel", "Starting fetch with criteria: $criteria")

                // Only show loading if we don't already have content
                if (state.value.displayState !is SearchResultsDisplayState.Content) {
                    _state.update { it.copy(displayState = SearchResultsDisplayState.Loading) }
                }

                // searchTransactions is now a plain fun returning Flow — no suspend call,
                // so setup + collection are all cancelled atomically with this job.
                transactionService.searchTransactions(criteria).collect { results ->
                    Log.d("SearchViewModel", "Successfully received ${results.size} date groups")
                    _state.update {
                        it.copy(displayState = SearchResultsDisplayState.Content(searchResults = results))
                    }
                }
            } catch (e: CancellationException) {
                Log.d("SearchViewModel", "Search job cancelled (this is normal)")
                // Don't update UI state on cancellation — the new job will handle it
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error fetching transactions: ${e.message}", e)
                _state.update {
                    it.copy(
                        displayState = SearchResultsDisplayState.Error(
                            message = e.message ?: "An unexpected error occurred"
                        )
                    )
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

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
        Log.d("SearchViewModel", "ViewModel cleared, search job cancelled")
    }
}
