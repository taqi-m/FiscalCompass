package com.fiscal.compass.presentation.screens.search

import com.fiscal.compass.domain.model.Transaction
import com.fiscal.compass.domain.model.base.Category
import com.fiscal.compass.domain.model.base.Person
import com.fiscal.compass.domain.util.SearchCriteria
import java.util.Date

/**
 * Top-level state for Search Results Screen following Data Class Wrapper pattern.
 * Shared data is at the top level, UI-specific state is in displayState sealed interface.
 */
data class SearchResultsState(
    // Shared data visible across all display states
    val searchCriteria: SearchCriteria = SearchCriteria(),
    val tempSearchCriteria: SearchCriteria = SearchCriteria(),
    val allCategories: List<Category> = emptyList(),
    val allPersons: List<Person> = emptyList(),

    // Display state for search results
    val displayState: SearchResultsDisplayState = SearchResultsDisplayState.Loading
)

/**
 * Sealed interface representing the display state of search results screen
 */
sealed interface SearchResultsDisplayState {
    data object Loading : SearchResultsDisplayState

    data class Error(val message: String) : SearchResultsDisplayState

    data class Content(
        val searchResults: Map<Date, List<Transaction>>
    ) : SearchResultsDisplayState
}

