package com.fiscal.compass.presentation.screens.search.navigation

/**
 * Sealed interface representing all possible navigation destinations from Search screens.
 * No JSON payloads on selection intents — all data is passed through the shared SearchViewModel state.
 */
sealed interface SearchNavigation {
    data object NavigateBack : SearchNavigation
    data object NavigateToFilters : SearchNavigation

    /**
     * Navigate to transaction detail screen.
     * @param transactionJson JSON string of transaction data
     */
    data class NavigateToTransactionDetail(
        val transactionJson: String
    ) : SearchNavigation

    // No payload — allCategories and tempSearchCriteria live in shared SearchViewModel state
    data object NavigateToCategorySelection : SearchNavigation

    // No payload — allPersons and tempSearchCriteria live in shared SearchViewModel state
    data object NavigateToPersonSelection : SearchNavigation
}