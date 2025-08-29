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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.data.model.PersonType
import com.app.uniqueplant.ui.components.input.GenericExposedDropDownMenu

@Composable
fun AddPersonDialog(
    onAddNewPerson: (String, String) -> Unit,
    onDismiss: () -> Unit
){
    val focusRequester = remember { FocusRequester() }
    val personName = remember { mutableStateOf("") }
    val personTypes = PersonType.getDefaultTypes()
    var selectedPersonType by remember { mutableStateOf(personTypes.firstOrNull()) }

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
                OutlinedTextField(
                    modifier = Modifier.focusRequester(focusRequester),
                    value = personName.value,
                    onValueChange = { personName.value = it },
                    label = { Text(text = "Name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
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
                    onAddNewPerson(personName.value, selectedPersonType!!)
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