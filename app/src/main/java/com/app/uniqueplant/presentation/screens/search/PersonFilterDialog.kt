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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PersonFilterDialog(
    persons: List<PersonItem>,
    onDismissRequest: () -> Unit,
    onPersonSelected: (Long, Boolean) -> Unit,
    onConfirmation: () -> Unit
) {
    val selectedPersons = remember { mutableStateMapOf<Long, Boolean>() }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Select Persons") },
        text = {
            Column {
                LazyColumn {
                    items(persons) { person ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedPersons[person.id] ?: false,
                                onCheckedChange = { isChecked ->
                                    selectedPersons[person.id] = isChecked
                                    onPersonSelected(person.id, isChecked)
                                }
                            )
                            Text(text = person.name)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmation()
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

data class PersonItem(val id: Long, val name: String)

