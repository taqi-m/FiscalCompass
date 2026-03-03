package com.fiscal.compass.presentation.screens.person.editperson

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fiscal.compass.domain.util.PersonType
import com.fiscal.compass.ui.components.LoadingProgress
import com.fiscal.compass.ui.components.input.DataEntryTextField
import com.fiscal.compass.ui.components.input.GenericExposedDropDownMenu
import com.fiscal.compass.ui.theme.FiscalCompassTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPersonScreen(
    state: EditPersonScreenState,
    onEvent: (EditPersonEvent) -> Unit,
    onNavigateBackWithResult: (String?) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Show error in snackbar
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            onEvent(EditPersonEvent.ClearError)
        }
    }

    // Navigate back with result on success
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onNavigateBackWithResult("Person updated successfully!")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Person") },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBackWithResult(null) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (state.isLoading) {
            LoadingProgress(true)
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DataEntryTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Name",
                    placeholder = "Enter name",
                    value = state.name,
                    onValueChange = { onEvent(EditPersonEvent.UpdateName(it)) },
                    enabled = !state.isLoading,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )

                DataEntryTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Contact",
                    placeholder = "Enter contact (optional)",
                    value = state.contact,
                    onValueChange = { onEvent(EditPersonEvent.UpdateContact(it)) },
                    enabled = !state.isLoading,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    )
                )

                GenericExposedDropDownMenu(
                    label = "Person Type",
                    options = state.availableTypes,
                    selectedOption = state.selectedType,
                    onOptionSelected = { onEvent(EditPersonEvent.UpdateType(it)) },
                    enabled = !state.isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onEvent(EditPersonEvent.Submit) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.name.isNotBlank() && !state.isLoading
                ) {
                    Text(
                        text = "Save Changes",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditPersonScreenPreview() {
    FiscalCompassTheme {
        EditPersonScreen(
            state = EditPersonScreenState(
                personId = "1",
                name = "John Doe",
                contact = "1234567890",
                selectedType = PersonType.CUSTOMER.name,
                availableTypes = PersonType.getDefaultTypes()
            ),
            onEvent = {},
            onNavigateBackWithResult = {}
        )
    }
}

