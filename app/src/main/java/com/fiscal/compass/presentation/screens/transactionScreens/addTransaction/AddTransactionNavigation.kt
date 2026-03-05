package com.fiscal.compass.presentation.screens.transactionScreens.addTransaction

/**
 * Sealed interface representing all possible navigation destinations from AddTransactionScreen.
 * No JSON payloads — all data is passed through the shared AddTransactionViewModel state.
 */
sealed interface AddTransactionNavigation {
    data object NavigateBack : AddTransactionNavigation
    data object NavigateToCategorySelection : AddTransactionNavigation
    data object NavigateToPersonSelection : AddTransactionNavigation

    /**
     * Navigate to amount screen.
     * @param editMode whether this is an edit operation
     */
    data class NavigateToAmountScreen(val editMode: Boolean = false) : AddTransactionNavigation
}


