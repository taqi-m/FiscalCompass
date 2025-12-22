package com.fiscal.compass.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiscal.compass.domain.service.TransactionService
import com.fiscal.compass.domain.usecase.categories.GetCategoriesUseCase
import com.fiscal.compass.domain.usecase.person.GetAllPersonsUseCase
import com.fiscal.compass.domain.usecase.transaction.SearchTransactionUC
import com.fiscal.compass.domain.util.DateRange
import com.fiscal.compass.presentation.mappers.toUi
import com.fiscal.compass.presentation.screens.category.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
    private val searchTransactionUC: SearchTransactionUC,
    private val transactionService: TransactionService,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getAllPersonsUseCase: GetAllPersonsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SearchScreenState())
    val state: StateFlow<SearchScreenState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadCategoriesAndPersons()
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
                val updatedCriteria = state.value.searchCriteria.apply {
                    setTransactionType(event.type)
                }
                updateState { copy(searchCriteria = updatedCriteria) }
            }
            is SearchEvent.SubmitFilterCategory -> {
                val updatedCriteria = state.value.searchCriteria.apply {
                    val category = state.value.allCategories.find { it.categoryId == event.categoryId }
                    if (category != null) {
                        val currentCategories = getCategories() ?: emptyList()
                        if (currentCategories.any { it.categoryId == event.categoryId }) {
                            setCategories(currentCategories.filter { it.categoryId != event.categoryId })
                        } else {
                            addCategory(category)
                        }
                    }
                }
                updateState { copy(searchCriteria = updatedCriteria) }
            }

            is SearchEvent.SubmitFilterPerson -> {
                val updatedCriteria = state.value.searchCriteria.apply {
                    val person = state.value.allPersons.find { it.personId == event.personId }
                    if (person != null) {
                        val currentPersons = getPersons() ?: emptyList()
                        if (currentPersons.any { it.personId == event.personId }) {
                            setPersons(currentPersons.filter { it.personId != event.personId })
                        } else {
                            addPerson(person)
                        }
                    }
                }
                updateState { copy(searchCriteria = updatedCriteria) }
            }

            is SearchEvent.StartDateSelected -> {
                val currentDateRange = state.value.searchCriteria.getDateRange()
                val newDateRange = DateRange.from(event.startDate, currentDateRange?.endDate)
                val updatedCriteria = state.value.searchCriteria.apply {
                    setDateRange(newDateRange)
                }
                updateState { copy(searchCriteria = updatedCriteria) }
            }

            is SearchEvent.EndDateSelected -> {
                val currentDateRange = state.value.searchCriteria.getDateRange()
                val newDateRange = DateRange.from(currentDateRange?.startDate, event.endDate)
                val updatedCriteria = state.value.searchCriteria.apply {
                    setDateRange(newDateRange)
                }
                updateState { copy(searchCriteria = updatedCriteria) }
            }

            SearchEvent.NavigateToCategorySelection -> {
                updateState { copy(navigateToCategorySelection = true) }
            }

            SearchEvent.NavigateToPersonSelection -> {
                updateState { copy(navigateToPersonSelection = true) }
            }

            SearchEvent.ResetNavigation -> {
                updateState {
                    copy(
                        navigateToCategorySelection = false,
                        navigateToPersonSelection = false
                    )
                }
            }

            is SearchEvent.UpdateSelectedCategories -> {
                val selectedCategories = state.value.allCategories.filter {
                    event.categoryIds.contains(it.categoryId)
                }
                val updatedCriteria = state.value.searchCriteria.apply {
                    setCategories(selectedCategories)
                }
                updateState {
                    copy(
                        searchCriteria = updatedCriteria,
                        navigateToCategorySelection = false
                    )
                }
            }

            is SearchEvent.UpdateSelectedPersons -> {
                val selectedPersons = state.value.allPersons.filter {
                    event.personIds.contains(it.personId)
                }
                val updatedCriteria = state.value.searchCriteria.apply {
                    setPersons(selectedPersons)
                }
                updateState {
                    copy(
                        searchCriteria = updatedCriteria,
                        navigateToPersonSelection = false
                    )
                }
            }

            SearchEvent.ApplyFilters -> {
                updateState { copy(showFilterDialog = false) }
                fetchTransactions()
            }
            SearchEvent.ClearFilters -> {
                updateState {
                    copy(searchCriteria = com.fiscal.compass.domain.util.SearchCriteria())
                }
                fetchTransactions()
            }
            is SearchEvent.UpdateDateRange -> {
                val updatedCriteria = state.value.searchCriteria.apply {
                    setDateRange(event.dateRange)
                }
                updateState { copy(searchCriteria = updatedCriteria) }
            }
        }
    }

    private fun fetchTransactions() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            updateState { copy(uiState = UiState.Loading) }
            try {
                val criteria = state.value.searchCriteria
                val results = transactionService.searchTransactions(
                    personIds = criteria.getPersonIds(),
                    categoryIds = criteria.getCategoryIds(),
                    startDate = criteria.getDateRange()?.startDate,
                    endDate = criteria.getDateRange()?.endDate,
                    filterType = criteria.getTransactionType()?.name
                )
                updateState { copy(uiState = UiState.Idle, searchResults = results) }
            } catch (e: Exception) {
                updateState { copy(uiState = UiState.Error(e.message ?: "An unexpected error occurred")) }
            }
        }
    }

    private fun loadCategoriesAndPersons() {
        viewModelScope.launch {
            try {
                val categories = getCategoriesUseCase.getAllCategories()
                val persons = getAllPersonsUseCase.getAllPersons()
                updateState { copy(allCategories = categories, allPersons = persons) }
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }

    private fun updateState(update: SearchScreenState.() -> SearchScreenState) {
        _state.value = _state.value.update()
    }
}
