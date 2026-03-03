package com.fiscal.compass.presentation.screens.search

/**
 * Sealed interface representing all possible navigation destinations from Search screens.
 * This allows for type-safe navigation without passing NavHostController directly to the screens.
 */
sealed interface SearchNavigation {
    /**
     * Navigate back to the previous screen
     */
    data object NavigateBack : SearchNavigation

    /**
     * Navigate to filters screen from results
     */
    data object NavigateToFilters : SearchNavigation

    /**
     * Navigate to transaction detail screen
     * @param transactionJson JSON string of transaction data
     */
    data class NavigateToTransactionDetail(
        val transactionJson: String
    ) : SearchNavigation

    /**
     * Navigate to category selection screen
     * @param allSelectableItemsJson JSON string of all selectable categories
     * @param selectedIds Comma-separated string of currently selected category IDs
     */
    data class NavigateToCategorySelection(
        val allSelectableItemsJson: String,
        val selectedIds: String
    ) : SearchNavigation

    /**
     * Navigate to person selection screen
     * @param allSelectableItemsJson JSON string of all selectable persons
     * @param selectedIds Comma-separated string of currently selected person IDs
     */
    data class NavigateToPersonSelection(
        val allSelectableItemsJson: String,
        val selectedIds: String
    ) : SearchNavigation
}

