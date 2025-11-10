package com.app.uniqueplant.ui.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.domain.model.PersonType
import com.app.uniqueplant.domain.model.base.Person
import com.app.uniqueplant.ui.components.input.DataEntryTextField
import com.app.uniqueplant.ui.components.input.GenericExposedDropDownMenu

@Composable
fun EditPersonDialog(
    person: Person?,
    onEditPerson: (Person) -> Unit,
    onDismiss: () -> Unit
) {

    if (person == null) {
        onDismiss()
        return
    }

    var personName by remember { mutableStateOf(person.name) }
    var personType by remember { mutableStateOf(person.personType) }
    var personContact by remember { mutableStateOf(person.contact ?: "") }
    val personTypes = PersonType.getDefaultTypes()
    var selectedPersonType by remember { mutableStateOf(personType) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "Edit Person"
            )
        },
        text = {
            Column (
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                DataEntryTextField(
                    label = "Name",
                    placeholder = "Enter new name",
                    value = personName,
                    onValueChange = { personName = it },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                )

                DataEntryTextField(
                    label = "Contact",
                    placeholder = "Enter contact",
                    value = personContact,
                    onValueChange = { personContact = it },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
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
                    onEditPerson(
                        person.copy(
                            name = personName,
                            personType = selectedPersonType
                        )
                    )
                    onDismiss()
                }
            ) {
                Text(
                    text = "Save"
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
fun EditPersonDialogPreview() {
    val person = Person(
        name = "John Doe",
        personType = "Customer"
    )
    EditPersonDialog(
        person = person,
        onEditPerson = {}
    ) {}
}