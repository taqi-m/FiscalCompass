package com.app.uniqueplant.presentation.screens.itemselection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.ui.theme.UniquePlantTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemSelectionScreen(
    state: ItemSelectionScreenState,
    onEvent: (ItemSelectionEvent) -> Unit,
    title: String,
    onConfirm: (List<SelectableItem>) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    searchPlaceholder: String = "Search..."
) {
    // Use derivedStateOf to prevent unnecessary recompositions when filtering
    val filteredItems by remember(state.searchQuery, state.allItems) {
        derivedStateOf { state.getFilteredItems() }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = {
                        onEvent(ItemSelectionEvent.CancelSelection)
                        onCancel()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        onEvent(ItemSelectionEvent.ConfirmSelection)
                        onConfirm(state.selectedItems.toList())
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Confirm"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search field
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { query ->
                    onEvent(ItemSelectionEvent.SearchQueryChanged(query))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text(searchPlaceholder) },
                singleLine = true
            )

            // Selected items count
            Text(
                text = "${state.selectedItems.size} item${if (state.selectedItems.size != 1) "s" else ""} selected",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Content
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                filteredItems.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (state.allItems.isEmpty()) {
                                "No items available"
                            } else {
                                "No items found"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(
                            items = filteredItems,
                            key = { _, item -> item.id }
                        ){ index, item ->
                            SelectableItemRow(
                                item = item,
                                isSelected = state.selectedItems.contains(item),
                                onToggle = { onEvent(ItemSelectionEvent.ItemToggled(item)) }
                            )

                            if (index < filteredItems.lastIndex)
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectableItemRow(
    item: SelectableItem,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() }
        )

        Text(
            text = item.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ItemSelectionScreenPreview() {
    val sampleItems = listOf(
        SelectableItem(id = "apple", name = "Apple"),
        SelectableItem(id = "banana", name = "Banana"),
        SelectableItem(id = "cherry", name = "Cherry"),
        SelectableItem(id = "date", name = "Date"),
        SelectableItem(id = "elderberry", name = "Elderberry"),
        SelectableItem(id = "fig", name = "Fig"),
        SelectableItem(id = "grape", name = "Grape"),
        SelectableItem(id = "honeydew", name = "Honeydew"),
        SelectableItem(id = "kiwi", name = "Kiwi"),
        SelectableItem(id = "lemon", name = "Lemon"),
        SelectableItem(id = "mango", name = "Mango"),
        SelectableItem(id = "orange", name = "Orange"),
        SelectableItem(id = "papaya", name = "Papaya"),
        SelectableItem(id = "quince", name = "Quince"),
        SelectableItem(id = "raspberry", name = "Raspberry")
    )

    val preSelectedItems = listOf(
        SelectableItem(id = "banana", name = "Banana"),
        SelectableItem(id = "date", name = "Date"),
        SelectableItem(id = "grape", name = "Grape")
    )

    UniquePlantTheme {
        ItemSelectionScreen(
            state = ItemSelectionScreenState(
                searchQuery = "",
                allItems = sampleItems,
                selectedItems = preSelectedItems.toSet(),
                isLoading = false,
                error = null
            ),
            onEvent = {},
            title = "Select Fruits",
            searchPlaceholder = "Search fruits...",
            onConfirm = {},
            onCancel = {}
        )
    }
}

@Preview(showBackground = true, name = "Empty State")
@Composable
fun ItemSelectionScreenEmptyPreview() {
    UniquePlantTheme {
        ItemSelectionScreen(
            state = ItemSelectionScreenState(
                searchQuery = "",
                allItems = emptyList(),
                selectedItems = emptySet(),
                isLoading = false,
                error = null
            ),
            onEvent = {},
            title = "Select Items",
            searchPlaceholder = "Search...",
            onConfirm = {},
            onCancel = {}
        )
    }
}

@Preview(showBackground = true, name = "No Results")
@Composable
fun ItemSelectionScreenNoResultsPreview() {
    val sampleItems = listOf(
        SelectableItem(id = "apple", name = "Apple"),
        SelectableItem(id = "banana", name = "Banana"),
        SelectableItem(id = "cherry", name = "Cherry")
    )

    UniquePlantTheme {
        ItemSelectionScreen(
            state = ItemSelectionScreenState(
                searchQuery = "xyz",
                allItems = sampleItems,
                selectedItems = emptySet(),
                isLoading = false,
                error = null
            ),
            onEvent = {},
            title = "Select Items",
            searchPlaceholder = "Search...",
            onConfirm = {},
            onCancel = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
fun ItemSelectionScreenLoadingPreview() {
    UniquePlantTheme {
        ItemSelectionScreen(
            state = ItemSelectionScreenState(
                searchQuery = "",
                allItems = emptyList(),
                selectedItems = emptySet(),
                isLoading = true,
                error = null
            ),
            onEvent = {},
            title = "Select Items",
            searchPlaceholder = "Search...",
            onConfirm = {},
            onCancel = {}
        )
    }
}

