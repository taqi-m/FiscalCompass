package com.fiscal.compass.presentation.screens.transactionScreens.addTransaction

/**
 * Sealed interface representing all possible navigation destinations from AddTransactionScreen.
 * This allows for type-safe navigation without passing NavHostController directly to the screen.
 */
sealed interface AddTransactionNavigation {
    /**
     * Navigate back to the previous screen
     */
    data object NavigateBack : AddTransactionNavigation

    /**
     * Navigate to category selection screen
     * @param categoriesJson JSON string of categories list
     * @param currentCategoryId Currently selected category ID
     */
    data class NavigateToCategorySelection(
        val categoriesJson: String,
        val currentCategoryId: String
    ) : AddTransactionNavigation

    /**
     * Navigate to person selection screen
     * @param personsJson JSON string of persons list
     * @param currentPersonId Currently selected person ID (nullable)
     */
    data class NavigateToPersonSelection(
        val personsJson: String,
        val currentPersonId: String?
    ) : AddTransactionNavigation

    /**
     * Navigate to amount screen
     * @param transactionJson JSON string of transaction data
     */
    data class NavigateToAmountScreen(
        val transactionJson: String
    ) : AddTransactionNavigation
}


