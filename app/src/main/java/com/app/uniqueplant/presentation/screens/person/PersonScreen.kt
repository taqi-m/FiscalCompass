package com.app.uniqueplant.presentation.screens.person

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.uniqueplant.R
import com.app.uniqueplant.domain.model.PersonType
import com.app.uniqueplant.domain.model.base.Person
import com.app.uniqueplant.presentation.screens.category.UiState
import com.app.uniqueplant.ui.components.LoadingProgress
import com.app.uniqueplant.ui.components.dialogs.AddPersonDialog
import com.app.uniqueplant.ui.components.dialogs.DeletePersonDialog
import com.app.uniqueplant.ui.components.dialogs.EditPersonDialog
import com.app.uniqueplant.ui.components.input.TypeSwitch
import com.app.uniqueplant.ui.theme.UniquePlantTheme

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
                onEvent(PersonEvent.OnUiReset)
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            if (state.canAdd){
                FloatingActionButton(
                    onClick = { onEvent(PersonEvent.OnPersonDialogToggle(PersonDialogToggle.Add)) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add_24),
                        contentDescription = "Add Transaction"
                    )
                }
            }
        }
    ) {
        PersonScreenContent(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 8.dp),
            state = state,
            onEvent = onEvent
        )
    }


    when (state.currentDialog) {
        PersonDialog.Hidden -> {}
        PersonDialog.AddPerson -> {
            AddPersonDialog(
                selectedType = state.selectedType,
                onDismiss = {
                    onEvent(PersonEvent.OnPersonDialogToggle(PersonDialogToggle.Hidden))
                },
                onAddNewPerson = { name, contact, personType ->
                    onEvent(
                        PersonEvent.OnPersonDialogSubmit(
                            PersonDialogSubmit.Add(
                                name,
                                contact,
                                personType
                            )
                        )
                    )
                }
            )
        }

        PersonDialog.EditPerson -> {
            EditPersonDialog(
                person = state.dialogState.person,
                onDismiss = {
                    onEvent(PersonEvent.OnPersonDialogToggle(PersonDialogToggle.Hidden))
                },
                onEditPerson = { editedPerson ->
                    onEvent(
                        PersonEvent.OnPersonDialogSubmit(PersonDialogSubmit.Edit(editedPerson))
                    )
                }
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
                        PersonEvent.OnPersonDialogSubmit(PersonDialogSubmit.Delete)
                    )
                }
            )
        }
    }

}


@Composable
fun PersonScreenContent(
    modifier: Modifier = Modifier,
    state: PersonScreenState,
    onEvent: (PersonEvent) -> Unit,
) {
    val typeOptions = PersonType.getDefaultTypes()
    val selectedTypeIndex = typeOptions.indexOf(state.selectedType).let {
        if (it == -1) 0 else it
    }
    val shape = MaterialTheme.shapes.small

    var personList = remember(state.persons, state.selectedType) {
        state.persons.filter { it.personType == state.selectedType }
    }
    LaunchedEffect(
        key1 = state.selectedType,
        key2 = state.persons
    ) {
        personList = state.persons.filter { it.personType == state.selectedType }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Top
    ) {
        TypeSwitch(
            modifier = Modifier
                .fillMaxWidth(),
            shape = shape,
            typeOptions = typeOptions,
            selectedTypeIndex = selectedTypeIndex,
            onTypeSelected = { index ->
                val selectedType = typeOptions.getOrNull(index) ?: PersonType.CUSTOMER.name
                onEvent(PersonEvent.OnFilterTypeSelected(selectedType))
            }
        )

        PersonList(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp),
            state = state,
            persons = personList,
            onEvent = onEvent,
            onAddNewPersonClick = if (state.canAdd) {
                { filterType ->
                    onEvent(PersonEvent.OnFilterTypeSelected(filterType))
                    onEvent(PersonEvent.OnPersonDialogToggle(PersonDialogToggle.Add))
                }
            } else null,
            onEditPersonClick = if (state.canEdit) {
                { person ->
                    onEvent(PersonEvent.OnPersonDialogToggle(PersonDialogToggle.Edit(person)))
                }
            } else null,
            onDeletePersonClick = if (state.canDelete) {
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
    state: PersonScreenState,
    onEvent: (PersonEvent) -> Unit,
    persons: List<Person> = emptyList(),
    onAddNewPersonClick: ((String) -> Unit)? = null,
    onEditPersonClick: ((Person) -> Unit)? = null,
    onDeletePersonClick: ((Person) -> Unit)? = null,
) {
    if (state.uiState is UiState.Loading) {
        LoadingProgress(true)
        return@PersonList
    }

    if (persons.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No persons available.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        return@PersonList
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = CenterHorizontally
    ) {
        itemsIndexed(items = persons, key = { _, person -> person.personId }) { index, person ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        enabled = onEditPersonClick != null,
                        onClick = {
                            onEditPersonClick?.invoke(person)
                        }
                    )
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
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
                    text = person.name,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                )
            }
            if (index < persons.lastIndex) {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            }
        }

        /*onAddNewPersonClick?.let {
            item {
                AddNewButton(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth()
                        .height(50.dp)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(4.dp)
                        ), text = "Add Person",
                    onClick = {
                        onAddNewPersonClick(state.selectedType)
                    })
            }
        }*/
    }
}


@Preview(showBackground = true)
@Composable
fun PersonScreenPreview() {
    UniquePlantTheme {
        PersonScreen(
            state = PersonScreenState(
                canAdd = true, persons = listOf(
                    Person(
                        personId = 1,
                        name = "John Doe",
                        contact = "1234567890",
                        personType = PersonType.CUSTOMER.name
                    ),
                    Person(
                        personId = 2,
                        name = "Jane Smith",
                        contact = "0987654321",
                        personType = PersonType.DEALER.name
                    ),
                    Person(
                        personId = 3,
                        name = "Alice Johnson",
                        contact = "5555555555",
                        personType = PersonType.CUSTOMER.name
                    ),
                )
            ),
            onEvent = {},
        )
    }
}