package com.fiscal.compass.presentation.screens.person

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiscal.compass.domain.model.rbac.Permission
import com.fiscal.compass.domain.service.PersonService
import com.fiscal.compass.domain.usecase.rbac.CheckPermissionUseCase
import com.fiscal.compass.domain.util.PersonType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonViewModel @Inject constructor(
    private val personService: PersonService,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_SELECTED_TYPE = "selected_type"
    }

    private suspend fun checkPermission(permission: Permission): Boolean {
        return checkPermissionUseCase(permission)
    }

    private val _state = MutableStateFlow(
        PersonState(
            selectedType = savedStateHandle.get<String>(KEY_SELECTED_TYPE) ?: PersonType.CUSTOMER.name
        )
    )
    val state: StateFlow<PersonState> = _state.asStateFlow()

    val coroutineScope = viewModelScope

    init {
        coroutineScope.launch(Dispatchers.IO) {
            _state.update {
                it.copy(
                    permissions = PersonPermissions(
                        canAdd = checkPermission(Permission.ADD_PERSON),
                        canEdit = checkPermission(Permission.EDIT_PERSON),
                        canDelete = checkPermission(Permission.DELETE_PERSON)
                    ),
                    displayState = PersonDisplayState.Loading
                )
            }
            updatePeople()
        }
    }

    fun onEvent(event: PersonEvent) {
        when (event) {
            is PersonEvent.OnUiReset -> {
                _state.update {
                    it.copy(
                        uiState = PersonUiState.Idle,
                        dialogState = PersonDialogState.Hidden
                    )
                }
            }

            is PersonEvent.OnPersonDialogToggle -> {
                onDialogToggle(event.event)
            }

            is PersonEvent.OnPersonDialogSubmit -> {
                onDialogSubmit(event.event)
            }

            is PersonEvent.OnFilterTypeSelected -> {
                // Save to SavedStateHandle for persistence across navigation
                savedStateHandle[KEY_SELECTED_TYPE] = event.selectedType

                _state.update {
                    val newState = it.copy(selectedType = event.selectedType)
                    // Update filtered persons when type changes
                    when (val displayState = newState.displayState) {
                        is PersonDisplayState.Content -> {
                            val filteredPersons = displayState.data.persons.filter { person ->
                                person.personType == event.selectedType
                            }
                            newState.copy(
                                displayState = PersonDisplayState.Content(
                                    displayState.data.copy(filteredPersons = filteredPersons)
                                )
                            )
                        }
                        else -> newState
                    }
                }
            }
        }
    }

    private fun updatePeople() {
        coroutineScope.launch {
            personService.getAllPersonsWithFlow().collect { personList ->
                _state.update { currentState ->
                    val filteredPersons = personList.filter { it.personType == currentState.selectedType }
                    currentState.copy(
                        displayState = PersonDisplayState.Content(
                            PersonContentData(
                                persons = personList,
                                filteredPersons = filteredPersons
                            )
                        )
                    )
                }
            }
        }
    }

    private fun onDialogToggle(event: PersonDialogToggle) {
        when (event) {

            is PersonDialogToggle.Delete -> {
                _state.update {
                    it.copy(dialogState = PersonDialogState.DeletePerson(event.person))
                }
            }

            PersonDialogToggle.Hidden -> {
                _state.update {
                    it.copy(dialogState = PersonDialogState.Hidden)
                }
            }
        }
    }

    private fun onDialogSubmit(event: PersonDialogSubmit) {
        when (event) {

            is PersonDialogSubmit.Delete -> {
                // Only proceed if we're in a valid state
                val currentDisplayState = _state.value.displayState
                if (currentDisplayState !is PersonDisplayState.Content) return

                val person = when (val dialogState = _state.value.dialogState) {
                    is PersonDialogState.DeletePerson -> dialogState.person
                    else -> null
                }
                if (person == null) {
                    _state.update {
                        it.copy(
                            uiState = PersonUiState.Error("No person selected for deletion."),
                            dialogState = PersonDialogState.Hidden
                        )
                    }
                    return
                }

                _state.update { it.copy(uiState = PersonUiState.Loading) }
                viewModelScope.launch {
                    val result = personService.deletePerson(person)
                    if (result.isFailure) {
                        _state.update {
                            it.copy(
                                uiState = PersonUiState.Error(
                                    "Failed to delete ${person.name}: ${result.exceptionOrNull()?.message ?: "Unknown error"}"
                                ),
                                dialogState = PersonDialogState.Hidden
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                uiState = PersonUiState.Success("${person.name} deleted successfully"),
                                dialogState = PersonDialogState.Hidden
                            )
                        }
                    }
                }
            }
        }
    }
}