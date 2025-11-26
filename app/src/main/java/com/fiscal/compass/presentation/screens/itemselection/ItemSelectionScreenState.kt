package com.fiscal.compass.presentation.screens.itemselection

data class ItemSelectionScreenState(
    val searchQuery: String = "",
    val allItems: List<SelectableItem> = emptyList(),
    val selectedItems: Set<SelectableItem> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    /**
     * Computed property for filtered items based on search query
     */
    fun getFilteredItems(): List<SelectableItem> {
        return if (searchQuery.isEmpty()) {
            allItems
        } else {
            allItems.filter { item ->
                item.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }
}

