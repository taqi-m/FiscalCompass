package com.fiscal.compass.presentation.screens.person

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fiscal.compass.R
import com.fiscal.compass.domain.model.base.Person
import com.fiscal.compass.domain.util.PersonType
import com.fiscal.compass.ui.components.LoadingProgress
import com.fiscal.compass.ui.components.dialogs.DeletePersonDialog
import com.fiscal.compass.ui.components.input.SingleSelectionChipGroup
import com.fiscal.compass.ui.theme.FiscalCompassTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonScreen(
    state: PersonState,
    onEvent: (PersonEvent) -> Unit,
    operationResultMessage: String? = null,
    onNavigate: (PersonNavigation) -> Unit = {}
) {
    val snackBarHostState = remember {
        SnackbarHostState()
    }

    // Handle operation result message from child screens
    LaunchedEffect(operationResultMessage) {
        operationResultMessage?.let {
            snackBarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
        }
    }

    // Handle UiState changes for operation feedback
    LaunchedEffect(state.uiState) {
        val message: String? = when (val uiState = state.uiState) {
            is PersonUiState.Error -> uiState.message
            is PersonUiState.Success -> uiState.message
            else -> null
        }
        message?.let { msg ->
            if (msg.isNotEmpty()) {
                snackBarHostState.showSnackbar(
                    message = msg,
                    duration = SnackbarDuration.Short,
                    actionLabel = "Dismiss"
                )
                onEvent(PersonEvent.OnUiReset)
            }
        }
    }

    // Handle error messages from display state
    LaunchedEffect(state.displayState) {
        if (state.displayState is PersonDisplayState.Error) {
            snackBarHostState.showSnackbar(
                message = state.displayState.message,
                duration = SnackbarDuration.Short,
                actionLabel = "Dismiss"
            )
            onEvent(PersonEvent.OnUiReset)
        }
    }

    Scaffold(
        floatingActionButton = {
            if (state.permissions.canAdd) {
                FloatingActionButton(
                    onClick = { onNavigate(PersonNavigation.NavigateToAddPerson(state.selectedType)) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add_24),
                        contentDescription = "Add Person"
                    )
                }
            }
        }) {
        // Content switching based on display state
        when (state.displayState) {
            is PersonDisplayState.Loading -> {
                LoadingProgress(true)
            }

            is PersonDisplayState.Error -> {
                PersonErrorContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 8.dp), errorMessage = state.displayState.message
                )
            }

            is PersonDisplayState.Content -> {
                PersonScreenContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    state = state,
                    contentData = state.displayState.data,
                    onEvent = onEvent,
                    onNavigate = onNavigate
                )

                // Show loading overlay during operations
                if (state.uiState is PersonUiState.Loading) {
                    LoadingProgress(true)
                }
            }
        }
    }

    // Dialog handling using unified dialog state
    when (state.dialogState) {
        PersonDialogState.Hidden -> {}

        is PersonDialogState.DeletePerson -> {
            DeletePersonDialog(personName = state.dialogState.person.name, onDismissRequest = {
                onEvent(PersonEvent.OnPersonDialogToggle(PersonDialogToggle.Hidden))
            }, onDeleteConfirm = {
                onEvent(
                    PersonEvent.OnPersonDialogSubmit(PersonDialogSubmit.Delete)
                )
            })
        }
    }
}

@Composable
fun PersonErrorContent(
    modifier: Modifier = Modifier, errorMessage: String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error: $errorMessage",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun PersonScreenContent(
    modifier: Modifier = Modifier,
    state: PersonState,
    contentData: PersonContentData,
    onEvent: (PersonEvent) -> Unit,
    onNavigate: (PersonNavigation) -> Unit,
) {
    val typeOptions = PersonType.getDefaultTypes()

    Column(
        modifier = modifier, horizontalAlignment = CenterHorizontally, verticalArrangement = Top
    ) {
        SingleSelectionChipGroup(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            maxLines = 1,
            items = typeOptions,
            chipToLabel = { it },
            initialSelection = state.selectedType,
            onSelectionChanged = {
                onEvent(PersonEvent.OnFilterTypeSelected(it))
            })

        HorizontalDivider(thickness = 2.dp)

        PersonList(
            modifier = Modifier
                .weight(1f),
            persons = contentData.filteredPersons,
            onEditPersonClick = if (state.permissions.canEdit) {
                { person ->
                    onNavigate(PersonNavigation.NavigateToEditPerson(person))
                }
            } else null,
            onDeletePersonClick = if (state.permissions.canDelete) {
                { person ->
                    onEvent(PersonEvent.OnPersonDialogToggle(PersonDialogToggle.Delete(person)))
                }
            } else null,
        )
    }
}


@Composable
fun PersonList(
    modifier: Modifier = Modifier,
    persons: List<Person> = emptyList(),
    onEditPersonClick: ((Person) -> Unit)? = null,
    onDeletePersonClick: ((Person) -> Unit)? = null,
) {
    if (persons.isEmpty()) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No persons available.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(), horizontalAlignment = CenterHorizontally
    ) {
        itemsIndexed(items = persons, key = { _, person -> person.personId }) { index, person ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(CardDefaults.shape)
                    .combinedClickable(
                        onClick = {
                            onEditPersonClick?.invoke(person)
                        },
                        onLongClick = {
                            onDeletePersonClick?.invoke(person)
                        }
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Surface(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(40.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = person.name.first().toString(),
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        text = person.name,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
            if (index < persons.lastIndex) {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PersonScreenPreview() {
    FiscalCompassTheme {
        PersonScreen(
            state = PersonState(
                permissions = PersonPermissions(canAdd = true),
                displayState = PersonDisplayState.Content(
                    PersonContentData(
                        persons = listOf(
                            Person(
                                personId = "1",
                                name = "John Doe",
                                contact = "1234567890",
                                personType = PersonType.CUSTOMER.name
                            ),
                            Person(
                                personId = "2",
                                name = "Jane Smith",
                                contact = "0987654321",
                                personType = PersonType.DEALER.name
                            ),
                            Person(
                                personId = "3",
                                name = "Alice Johnson",
                                contact = "5555555555",
                                personType = PersonType.CUSTOMER.name
                            ),
                        ), filteredPersons = listOf(
                            Person(
                                personId = "1",
                                name = "John Doe",
                                contact = "1234567890",
                                personType = PersonType.CUSTOMER.name
                            ),
                            Person(
                                personId = "3",
                                name = "Alice Johnson",
                                contact = "5555555555",
                                personType = PersonType.CUSTOMER.name
                            ),
                        )
                    )
                )
            ),
            onEvent = {},
            onNavigate = {}
        )
    }
}