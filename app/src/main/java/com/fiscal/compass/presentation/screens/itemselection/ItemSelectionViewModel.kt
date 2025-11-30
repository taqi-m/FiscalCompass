package com.fiscal.compass.presentation.screens.itemselection

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ItemSelectionViewModel @Inject constructor(
    // Add your dependencies here if needed
) : ViewModel() {

    private val _state = MutableStateFlow(ItemSelectionScreenState())
    val state: StateFlow<ItemSelectionScreenState> = _state.asStateFlow()

    /**
     * Returns the current list of selected items
     */
    fun getSelectedItems(): List<SelectableItem> = _state.value.selectedItems.toList()

    fun onEvent(event: ItemSelectionEvent) {
        when (event) {
            is ItemSelectionEvent.SearchQueryChanged -> {
                updateState { copy(searchQuery = event.query) }
            }

            is ItemSelectionEvent.ItemToggled -> {
                val item = event.item
                updateState {
                    val newSelectedItems = if (singleSelectionMode) {
                        // In single selection mode, replace the selection
                        setOf(item)
                    } else {
                        // In multi-selection mode, toggle the item
                        if (selectedItems.contains(item)) {
                            selectedItems - item
                        } else {
                            selectedItems + item
                        }
                    }
                    copy(selectedItems = newSelectedItems)
                }
            }

            is ItemSelectionEvent.InitializeScreen -> {
                updateState {
                    copy(
                        allItems = event.allItems,
                        selectedItems = event.preSelectedItems.toSet(),
                        searchQuery = "",
                        singleSelectionMode = event.singleSelectionMode
                    )
                }
            }

            ItemSelectionEvent.ConfirmSelection -> {
                // Navigation should be handled by the parent/caller
                // This event serves as a trigger for the parent to read selected items
            }

            ItemSelectionEvent.CancelSelection -> {
                // Navigation should be handled by the parent/caller
            }
        }
    }

    private fun updateState(update: ItemSelectionScreenState.() -> ItemSelectionScreenState) {
        _state.value = _state.value.update()
    }
}

