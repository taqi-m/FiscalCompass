package com.fiscal.compass.ui.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
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
import com.fiscal.compass.domain.model.PersonType
import com.fiscal.compass.ui.components.input.DataEntryTextField
import com.fiscal.compass.ui.components.input.GenericExposedDropDownMenu

@Composable
fun AddPersonDialog(
    selectedType: String? = null,
    onAddNewPerson: (name: String, contact: String, type: String) -> Unit,
    onDismiss: () -> Unit
){
    val focusRequester = remember { FocusRequester() }
    var personName by remember { mutableStateOf("") }
    var personContact by remember { mutableStateOf("") }
    val personTypes = PersonType.getDefaultTypes()
    var selectedPersonType by remember { mutableStateOf(personTypes.find { it == selectedType } ?: personTypes.firstOrNull()) }

    if (selectedPersonType == null) {
        return
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Add New Person") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                DataEntryTextField(
                    modifier = Modifier.focusRequester(focusRequester),
                    label = "Name",
                    placeholder = "Enter name",
                    value = personName,
                    onValueChange = { personName = it },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )


                DataEntryTextField(
                    label = "Contact",
                    placeholder = "Enter contact",
                    value = personContact,
                    onValueChange = { personContact = it },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    )
                )


                GenericExposedDropDownMenu(
                    label = "Person Type",
                    options = personTypes,
                    selectedOption = selectedPersonType,
                    onOptionSelected = { selectedType ->
                        selectedPersonType = selectedType
                    }
                )


            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onAddNewPerson(personName, personContact, selectedPersonType!!)
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
fun AddPersonDialogPreview() {
    AddPersonDialog(
        selectedType = "Client",
        onAddNewPerson = { _, _, _ -> },
        onDismiss = {}
    )
}
