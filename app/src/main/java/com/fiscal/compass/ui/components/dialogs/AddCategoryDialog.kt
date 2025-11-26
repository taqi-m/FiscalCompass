package com.fiscal.compass.ui.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fiscal.compass.domain.model.PersonType
import com.fiscal.compass.ui.components.input.DataEntryTextField

@Composable
fun AddCategoryDialog(
    onAddNewCategory: (String, String, String) -> Unit,
    onDismiss: () -> Unit
){
    val focusRequester = remember { FocusRequester() }
    var categoryName by remember { mutableStateOf("") }
    var categoryDescription by remember { mutableStateOf("") }

    val personTypes = PersonType.getDefaultTypes()

    var expectedPersonType by remember { mutableStateOf(personTypes.first()) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Add category", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DataEntryTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = "Title",
                    placeholder = "Category name",
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),

                )

                DataEntryTextField(
                    value = categoryDescription,
                    onValueChange = { categoryDescription = it },
                    label = "Description",
                    placeholder = "Category description",
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onAddNewCategory(categoryName, categoryDescription, expectedPersonType)
                    onDismiss()
                }
            ) {
                Text(
                    text = "Add"
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Cancel"
                )
            }
        }
    )
}

@Preview
@Composable
fun AddCategoryDialogPreview() {
    AddCategoryDialog(
        onAddNewCategory = { _, _, _ -> },
        onDismiss = {}
    )
}