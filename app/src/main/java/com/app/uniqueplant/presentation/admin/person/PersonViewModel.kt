package com.app.uniqueplant.presentation.admin.person

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.uniqueplant.domain.usecase.person.AddPersonUseCase
import com.app.uniqueplant.domain.usecase.person.DeletePersonUseCase
import com.app.uniqueplant.domain.usecase.person.GetAllPersonsUseCase
import com.app.uniqueplant.presentation.admin.categories.UiState
import com.app.uniqueplant.presentation.mappers.toDomain
import com.app.uniqueplant.presentation.mappers.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonViewModel @Inject constructor(
    private val getAllPersonsUseCase: GetAllPersonsUseCase,
    private val addPersonUseCase: AddPersonUseCase,
    private val deletePersonUseCase: DeletePersonUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PersonScreenState())
    val state: StateFlow<PersonScreenState> = _state.asStateFlow()

    val coroutineScope = viewModelScope

    init {
        coroutineScope.launch {
            updatePeople()
        }
    }

    fun onEvent(event: PersonEvent) {
        when (event) {
            is PersonEvent.OnUiReset -> {
                updateState {
                    copy(
                        uiState = UiState.Idle
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
                updateState { copy(persons = personList.map { it.toUi() }) }
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
                        personType = personType
                    )
                    updateState {
                        copy(
                            uiState = updatedState,
                            currentDialog = PersonDialog.Hidden,
                            dialogState = PersonDialogState.Idle
                        )
                    }
                }
            }

            is PersonDialogSubmit.Edit -> {
                // Handle edit person logic
            }

            is PersonDialogSubmit.Delete -> {
                val person = _state.value.dialogState.person?.toDomain()
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
                            uiState = updatedState,
                            currentDialog = PersonDialog.Hidden,
                            dialogState = PersonDialogState.Idle
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