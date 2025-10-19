package com.app.uniqueplant.ui.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.domain.model.PersonType

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
        title = { Text(text = "Add New Category") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OutlinedTextField(
                    modifier = Modifier.focusRequester(focusRequester),
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text(text = "Name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )

                /*GenericExposedDropDownMenu(
                    label = "Expected Person Type",
                    options = personTypes,
                    selectedOption = expectedPersonType,
                    onOptionSelected = { expectedPersonType = it }
                )*/

                OutlinedTextField(
                    value = categoryDescription,
                    onValueChange = { categoryDescription = it },
                    label = { Text(text = "Description") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    )
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