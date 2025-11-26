package com.fiscal.compass.presentation.screens.person

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiscal.compass.data.rbac.Permission
import com.fiscal.compass.domain.usecase.person.AddPersonUseCase
import com.fiscal.compass.domain.usecase.person.DeletePersonUseCase
import com.fiscal.compass.domain.usecase.person.EditPersonUC
import com.fiscal.compass.domain.usecase.person.GetAllPersonsUseCase
import com.fiscal.compass.domain.usecase.rbac.CheckPermissionUseCase
import com.fiscal.compass.presentation.screens.category.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonViewModel @Inject constructor(
    private val getAllPersonsUseCase: GetAllPersonsUseCase,
    private val addPersonUseCase: AddPersonUseCase,
    private val deletePersonUseCase: DeletePersonUseCase,
    private val editPersonUseCase: EditPersonUC,
    private val checkPermissionUseCase: CheckPermissionUseCase
) : ViewModel() {

    private suspend fun checkPermission(permission: Permission): Boolean {
        return checkPermissionUseCase(permission)
    }

    private val _state = MutableStateFlow(PersonScreenState())
    val state: StateFlow<PersonScreenState> = _state.asStateFlow()

    val coroutineScope = viewModelScope

    init {
        coroutineScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(
                canAdd = checkPermission(Permission.ADD_PERSON),
                canEdit = checkPermission(Permission.EDIT_PERSON),
                canDelete = checkPermission(Permission.DELETE_PERSON),
                uiState = UiState.Idle
            )
            updatePeople()
        }
    }

    fun onEvent(event: PersonEvent) {
        when (event) {
            is PersonEvent.OnUiReset -> {
                updateState {
                    copy(
                        uiState = UiState.Idle,
                        currentDialog = PersonDialog.Hidden,
                        dialogState = PersonDialogState.Idle
                    )
                }
            }

            is PersonEvent.OnPersonDialogToggle -> {
                onDialogToggle(event.event)
            }

            is PersonEvent.OnPersonDialogSubmit -> {
                onDialogSubmit(event.event)
            }

            is PersonEvent.OnPersonDialogStateChange -> {
                updateState { copy(dialogState = event.state) }
            }

            is PersonEvent.OnFilterTypeSelected -> {
                updateState { copy(selectedType = event.selectedType) }
            }
        }
    }

    private fun updatePeople() {
        coroutineScope.launch {
            getAllPersonsUseCase.getAllPersonsWithFlow().collect { personList ->
                updateState { copy(persons = personList) }
            }
        }
    }

    private fun onDialogToggle(event: PersonDialogToggle) {
        when (event) {
            is PersonDialogToggle.Add -> {
                updateState { copy(currentDialog = PersonDialog.AddPerson) }
            }

            is PersonDialogToggle.Edit -> {
                updateState {
                    copy(
                        currentDialog = PersonDialog.EditPerson,
                        dialogState = PersonDialogState(
                            person = event.person
                        )
                    )
                }
            }

            is PersonDialogToggle.Delete -> {
                updateState {
                    copy(
                        currentDialog = PersonDialog.DeletePerson,
                        dialogState = PersonDialogState(
                            person = event.person
                        )
                    )
                }
            }

            PersonDialogToggle.Hidden -> {
                updateState {
                    copy(
                        currentDialog = PersonDialog.Hidden,
                        dialogState = PersonDialogState.Idle
                    )
                }
            }
        }
    }

    private fun onDialogSubmit(event: PersonDialogSubmit) {
        when (event) {
            is PersonDialogSubmit.Add -> {
                updateState { copy(uiState = UiState.Loading) }
                val personType = _state.value.selectedType
                viewModelScope.launch {
                    val updatedState = addPersonUseCase.invoke(
                        name = event.name,
                        contact = event.contact,
                        personType = personType
                    )
                    updateState {
                        copy(uiState = updatedState)
                    }
                }
            }

            is PersonDialogSubmit.Edit -> {
                val person = event.person
                updateState { copy(uiState = UiState.Loading) }
                viewModelScope.launch {
                    try {
                        editPersonUseCase.invoke(person.personId, person.name, person.contact ?: "", person.personType)
                        updateState {
                            copy(uiState = UiState.Success("Person edited successfully."))
                        }
                    } catch (e: Exception) {
                        updateState {
                            copy(uiState = UiState.Error("Failed to edit person: ${e.message}"))
                        }
                    }
                }
            }

            is PersonDialogSubmit.Delete -> {
                val person = _state.value.dialogState.person
                if (person == null) {
                    updateState {
                        copy(uiState = UiState.Error("No person selected for deletion."))
                    }
                    return
                }
                updateState { copy(uiState = UiState.Loading) }
                viewModelScope.launch {
                    val updatedState = deletePersonUseCase.invoke(person)
                    updateState {
                        copy(
                            uiState = updatedState
                        )
                    }
                }
            }
        }
    }

    private fun updateState(update: PersonScreenState.() -> PersonScreenState) {
        _state.value = _state.value.update()
    }
}