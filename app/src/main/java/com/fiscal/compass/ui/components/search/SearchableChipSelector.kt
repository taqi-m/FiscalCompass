package com.fiscal.compass.ui.components.search

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fiscal.compass.ui.components.cards.ChipFlow
import com.fiscal.compass.ui.theme.FiscalCompassTheme

/**
 * A generic searchable chip selector component that can be used for any type of data
 *
 * @param T The type of data to search and select
 * @param title The title to display above the search field
 * @param searchQuery The current search query
 * @param showDropdown Whether to show the dropdown menu
 * @param allItems All available items to search from
 * @param selectedItems The currently selected items to display as chips
 * @param searchPlaceholder Placeholder text for the search field
 * @param emptyMessage Message to show when no items are found
 * @param itemToLabel Function to convert an item to its display label
 * @param onSearchQueryChanged Callback when search query changes
 * @param onDropdownVisibilityChanged Callback when dropdown visibility changes
 * @param onItemSelected Callback when an item is selected from dropdown
 * @param onChipClicked Callback when a selected chip is clicked (for removal)
 * @param modifier Modifier for the component
 */
@Composable
fun <T : Any> SearchableChipSelector(
    modifier: Modifier = Modifier,
    title: String,
    searchQuery: String,
    showDropdown: Boolean,
    allItems: List<T>,
    selectedItems: List<T>,
    searchPlaceholder: String,
    emptyMessage: String = "No items found",
    itemToLabel: (T) -> String,
    onSearchQueryChanged: (String) -> Unit,
    onDropdownVisibilityChanged: (Boolean) -> Unit,
    onItemSelected: (T) -> Unit,
    onChipClicked: (T) -> Unit
) {
    // Use derivedStateOf to prevent unnecessary recompositions
    val filteredItems by remember(allItems, searchQuery) {
        derivedStateOf {
            allItems.filter { item ->
                itemToLabel(item).contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )

        // Separate the search field and dropdown to prevent recomposition issues
        SearchFieldWithDropdown(
            searchQuery = searchQuery,
            showDropdown = showDropdown,
            searchPlaceholder = searchPlaceholder,
            filteredItems = filteredItems,
            emptyMessage = emptyMessage,
            itemToLabel = itemToLabel,
            onSearchQueryChanged = onSearchQueryChanged,
            onDropdownVisibilityChanged = onDropdownVisibilityChanged,
            onItemSelected = onItemSelected
        )

        if (selectedItems.isNotEmpty()){
            ChipFlow(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                placeholder = "",
                chips = selectedItems,
                onChipClick = onChipClicked,
                chipToLabel = itemToLabel
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}

@Composable
private fun <T : Any> SearchFieldWithDropdown(
    searchQuery: String,
    showDropdown: Boolean,
    searchPlaceholder: String,
    filteredItems: List<T>,
    emptyMessage: String,
    itemToLabel: (T) -> String,
    onSearchQueryChanged: (String) -> Unit,
    onDropdownVisibilityChanged: (Boolean) -> Unit,
    onItemSelected: (T) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    // Use internal state to prevent recomposition from parent affecting the TextField
    var internalQuery by remember { mutableStateOf(searchQuery) }
    val currentSearchQuery by rememberUpdatedState(searchQuery)

    // Sync internal state with external state when it changes externally (e.g., clear)
    if (currentSearchQuery != internalQuery && currentSearchQuery.isEmpty()) {
        internalQuery = currentSearchQuery
    }

    Box {
        // Use key to prevent TextField from losing focus when other parts recompose
        key("search-text-field") {
            OutlinedTextField(
                value = internalQuery,
                onValueChange = { query ->
                    internalQuery = query
                    onSearchQueryChanged(query)
                    onDropdownVisibilityChanged(query.isNotEmpty())
                },
                placeholder = { Text(searchPlaceholder) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused && internalQuery.isNotEmpty()) {
                            onDropdownVisibilityChanged(true)
                        }
                    },
                singleLine = true
            )
        }

        // Separate the dropdown from the text field to minimize recomposition impact
        if (showDropdown) {
            DropdownMenu(
                expanded = true,
                onDismissRequest = {
                    onDropdownVisibilityChanged(false)
                },
                modifier = Modifier
                    .heightIn(max = 200.dp)
                    .fillMaxWidth(0.9f)
            ) {
                if (filteredItems.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text(emptyMessage) },
                        onClick = { }
                    )
                } else {
                    filteredItems.take(10).forEach { item ->
                        DropdownMenuItem(
                            text = { Text(itemToLabel(item)) },
                            onClick = {
                                onItemSelected(item)
                                internalQuery = ""
                                onSearchQueryChanged("")
                                onDropdownVisibilityChanged(false)
                            }
                        )
                    }

                    if (filteredItems.size > 10) {
                        DropdownMenuItem(
                            text = { Text("... and ${filteredItems.size - 10} more", style = MaterialTheme.typography.bodySmall) },
                            onClick = { }
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
)
@Composable
fun SearchableChipSelectorPreview() {
    val allItems = listOf(
        "Apple", "Banana", "Cherry", "Date", "Elderberry",
        "Fig", "Grape", "Honeydew", "Kiwi", "Lemon",
        "Mango", "Orange", "Papaya", "Quince", "Raspberry",
        "Strawberry", "Tangerine", "Ugli fruit", "Watermelon", "Xigua"
    )
    FiscalCompassTheme{
        SearchableChipSelector(
            modifier = Modifier.padding(16.dp),
            title = "Fruits",
            searchQuery = "",
            showDropdown = false,
            allItems = allItems,
            selectedItems = listOf("Banana", "Date"),
            searchPlaceholder = "Search for a fruit",
            emptyMessage = "No fruits found",
            itemToLabel = { it },
            onSearchQueryChanged = {},
            onDropdownVisibilityChanged = {},
            onItemSelected = {},
            onChipClicked = {}
        )
    }
}
