package com.app.uniqueplant.presentation.admin.person

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.R
import com.app.uniqueplant.data.model.PersonTypes
import com.app.uniqueplant.presentation.admin.categories.UiState
import com.app.uniqueplant.ui.components.LoadingProgress
import com.app.uniqueplant.ui.components.cards.PersonItem
import com.app.uniqueplant.ui.components.dialogs.AddPersonDialog
import com.app.uniqueplant.ui.components.dialogs.DeletePersonDialog
import com.app.uniqueplant.ui.components.input.GenericExposedDropDownMenu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonScreen(
    state: PersonScreenState,
    onEvent: (PersonEvent) -> Unit,
) {
    val snackBarHostState = remember {
        SnackbarHostState()
    }

    val uiState = state.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Persons") },
                actions = {
                    Button(
                        onClick = {
                            onEvent(PersonEvent.OnPersonDialogToggle(PersonDialogToggle.Add))
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(text = "Add Person")
                    }
                },
                navigationIcon = {
                    // You can add a navigation icon here if needed
                    IconButton(onClick = {

                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back_24),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }
    )
    {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            LaunchedEffect(uiState) {
                val message: String? = when (uiState) {
                    is UiState.Error -> uiState.message
                    is UiState.Success -> uiState.message
                    else -> null
                }
                message?.let { msg ->
                    if (msg.isNotEmpty()) {
                        snackBarHostState.showSnackbar(
                            message = msg,
                            duration = SnackbarDuration.Short,
                            actionLabel = "Dismiss"
                        )
                        Log.d(
                            "CategoriesScreen",
                            "Snackbar message: $snackBarHostState.currentSnackbarData?.visuals?.message"
                        )
                        onEvent(PersonEvent.OnUiReset)
                    }
                }
            }
            PersonScreenContent(
                modifier = Modifier
                    .fillMaxSize(),
                state = state,
                onEvent = onEvent
            )

            when (state.currentDialog) {
                PersonDialog.Hidden -> {}
                PersonDialog.AddPerson -> {
                    AddPersonDialog(
                        onDismiss = {
                            onEvent(PersonEvent.OnPersonDialogToggle(PersonDialogToggle.Hidden))
                        },
                        onAddNewPerson = { name, personType ->
                            onEvent(
                                PersonEvent.OnPersonDialogSubmit(
                                    PersonDialogSubmit.Add(name, personType)
                                )
                            )
                        }
                    )
                }

                PersonDialog.EditPerson -> {
                    TODO(
                        "Handle EditPerson dialog in PersonScreen.kt"
                    )
                }

                PersonDialog.DeletePerson -> {
                    DeletePersonDialog(
                        personName = state.dialogState.person?.name,
                        onDismissRequest = {
                            onEvent(PersonEvent.OnPersonDialogToggle(PersonDialogToggle.Hidden))
                        },
                        onDeleteConfirm = {
                            onEvent(
                                PersonEvent.OnPersonDialogSubmit(
                                    PersonDialogSubmit.Delete
                                )
                            )
                        }
                    )
                }
            }
        }
    }

}


@Composable
fun PersonScreenContent(
    modifier: Modifier = Modifier,
    state: PersonScreenState,
    onEvent: (PersonEvent) -> Unit,
) {
    PersonList(
        state = state,
        onEvent = onEvent
    )

}


@Composable
fun PersonList(
    state: PersonScreenState,
    onEvent: (PersonEvent) -> Unit,
) {
    if (state.uiState is UiState.Loading) {
        LoadingProgress(
            true
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                GenericExposedDropDownMenu(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    label = "Filter by Type",
                    selectedOption = state.selectedType,
                    options = PersonTypes.getDefaultTypes(),
                    onOptionSelected = { selectedType ->
                        onEvent(PersonEvent.OnFilterTypeSelected(selectedType))
                    }
                )
            }

            when (state.persons.isEmpty()) {
                true -> {
                    item {
                        Text(
                            text = "No person available for ${state.selectedType}.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    items(state.persons) { person ->
                        PersonItem(
                            personName = person.name,
                            personType = person.personType,
                            onEditClick = {
                                onEvent(
                                    PersonEvent.OnPersonDialogToggle(
                                        PersonDialogToggle.Edit(
                                            person
                                        )
                                    )
                                )
                            },
                            onDeleteClicked = {
                                onEvent(
                                    PersonEvent.OnPersonDialogToggle(
                                        PersonDialogToggle.Delete(
                                            person
                                        )
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }
            }
        }

    }
}


@Preview(showBackground = true)
@Composable
fun PersonScreenPreview() {
    PersonScreen(
        state = PersonScreenState(),
        onEvent = {},
    )
}