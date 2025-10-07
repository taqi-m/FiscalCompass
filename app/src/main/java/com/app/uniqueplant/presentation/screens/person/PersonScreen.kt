package com.app.uniqueplant.presentation.screens.person

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.uniqueplant.R
import com.app.uniqueplant.data.local.model.PersonType
import com.app.uniqueplant.presentation.model.PersonUi
import com.app.uniqueplant.presentation.screens.categories.UiState
import com.app.uniqueplant.ui.components.LoadingProgress
import com.app.uniqueplant.ui.components.cards.ExpandableChipCard
import com.app.uniqueplant.ui.components.dialogs.AddPersonDialog
import com.app.uniqueplant.ui.components.dialogs.DeletePersonDialog
import com.app.uniqueplant.ui.theme.UniquePlantTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonScreen(

    appNavController: NavHostController,
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
        topBar = {
            TopAppBar(
                title = { Text(text = "Persons") },
                navigationIcon = {

                    IconButton(
                        onClick = {
                            appNavController.popBackStack()
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back_24),
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
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
                .padding(horizontal = 8.dp)

        ) {
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
                        selectedType = state.selectedType,
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


@Composable
fun PersonList(
    state: PersonScreenState,
    onEvent: (PersonEvent) -> Unit,
    onAddNewPersonClick: ((String) -> Unit)? = null,
    onEditPersonClick: ((PersonUi) -> Unit)? = null,
    onDeletePersonClick: ((PersonUi) -> Unit)? = null,
) {
    if (state.uiState is UiState.Loading) {
        LoadingProgress(
            true
        )
    } else {
        val personTypes = PersonType.getDefaultTypes()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            personTypes.forEach { personType ->
                item {
                    ExpandableChipCard(
                        modifier = Modifier
                            .fillMaxWidth(),
                        title = personType,
                        trailingIcon = onAddNewPersonClick?.let{
                            {
                                FilledTonalIconButton(
                                    modifier = Modifier
                                        .height(32.dp)
                                        .width(32.dp),
                                    shape = RoundedCornerShape(15),
                                    onClick = {
                                        onAddNewPersonClick(personType)
                                    }
                                ) {
                                    Icon(
                                        modifier = Modifier.height(24.dp),
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Filter"
                                    )
                                }
                            }
                        },
                        chips = state.persons.filter { it.personType == personType },
                        onChipClick = { person ->
                            onEditPersonClick?.invoke(person)
                        },
                        onChipLongClick = { person ->
                            onDeletePersonClick?.invoke(person)
                        },
                        initiallyExpanded = false,
                        chipToLabel = { it.name }
                    )
                }
            }
        }

    }
}


@Preview(showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun PersonScreenPreview() {
    UniquePlantTheme {
        PersonScreen(
            appNavController = rememberNavController(),
            state = PersonScreenState(canAdd = true, persons = PersonUi.dummyList),
            onEvent = {},
        )
    }
}