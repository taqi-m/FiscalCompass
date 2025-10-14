package com.app.uniqueplant.presentation.screens.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T: Any> MultiSelectDialog(
    items: List<T>,
    selectedItems: List<T>,
    itemToLabel: (T) -> String,
    onDismissRequest: () -> Unit,
    onConfirmation: (List<T>) -> Unit
) {
    val tempSelectedItems = remember { mutableStateMapOf<T, Boolean>() }

    LaunchedEffect(selectedItems) {
        tempSelectedItems.clear()
        selectedItems.forEach { item ->
            tempSelectedItems[item] = true
        }
    }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Select Categories") },
        text = {
            Column {
                LazyColumn {
                    items(items) { item ->
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
            Button(
                onClick = {
                    onConfirmation(tempSelectedItems.filter { it.value }.keys.toList())
                    onDismissRequest()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}

