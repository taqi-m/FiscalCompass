package com.fiscal.compass.presentation.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun <T : Any> MultiSelectDialog(
    items: List<T>,
    selectedItems: List<T>,
    itemToLabel: (T) -> String,
    onDismissRequest: () -> Unit,
    onConfirmation: (List<T>) -> Unit
) {
    val tempSelectedItems = remember { mutableStateMapOf<T, Boolean>() }
    var searchQuery by remember { mutableStateOf("") }

    // Filter items based on search query
    val filteredItems = remember(items, searchQuery) {
        if (searchQuery.isBlank()) {
            items
        } else {
            items.filter { item ->
                itemToLabel(item).contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LaunchedEffect(selectedItems) {
        tempSelectedItems.clear()
        selectedItems.forEach { item ->
            tempSelectedItems[item] = true
        }
    }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text("Select Categories")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                LazyColumn {
                    items(filteredItems) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = tempSelectedItems[item] ?: false,
                                onCheckedChange = { isChecked ->
                                    tempSelectedItems[item] = isChecked
                                }
                            )
                            Text(text = itemToLabel(item))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ){
                OutlinedButton (
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium,
                    onClick = onDismissRequest) {
                    Text("Cancel")
                }
                Button(
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium,
                    onClick = {
                        onConfirmation(tempSelectedItems.filter { it.value }.keys.toList())
                        onDismissRequest()
                    }
                ) {
                    Text("Done")
                }
            }
        },
        dismissButton = {

        }
    )
}

@Preview
@Composable
fun MultiSelectDialogPreview() {
    val items = listOf("Category 1", "Category 2", "Category 3", "Category 4", "Category 5")
    val selectedItems = listOf("Category 2", "Category 4")
    MultiSelectDialog(
        items = items,
        selectedItems = selectedItems,
        itemToLabel = { it },
        onDismissRequest = {},
        onConfirmation = {}
    )
}

