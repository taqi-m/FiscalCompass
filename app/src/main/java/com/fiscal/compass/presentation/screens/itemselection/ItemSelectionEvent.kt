package com.fiscal.compass.presentation.screens.itemselection

sealed class ItemSelectionEvent {
    data class SearchQueryChanged(val query: String) : ItemSelectionEvent()
    data class ItemToggled(val item: SelectableItem) : ItemSelectionEvent()
    data object ConfirmSelection : ItemSelectionEvent()
    data object CancelSelection : ItemSelectionEvent()
    data class InitializeScreen(
        val allItems: List<SelectableItem>,
        val preSelectedItems: List<SelectableItem>,
        val singleSelectionMode: Boolean = false
    ) : ItemSelectionEvent()
}

