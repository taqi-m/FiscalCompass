package com.fiscal.compass.presentation.screens.person.editperson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiscal.compass.domain.model.base.Person
import com.fiscal.compass.domain.service.PersonService
import com.fiscal.compass.domain.util.PersonType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPersonViewModel @Inject constructor(
    private val personService: PersonService
) : ViewModel() {

    private val _state = MutableStateFlow(EditPersonScreenState())
    val state: StateFlow<EditPersonScreenState> = _state.asStateFlow()

    init {
        _state.update {
            it.copy(availableTypes = PersonType.getDefaultTypes())
        }
    }

    fun onEvent(event: EditPersonEvent) {
        when (event) {
            is EditPersonEvent.LoadPerson -> {
                loadPerson(event.person)
            }
            is EditPersonEvent.UpdateName -> {
                _state.update { it.copy(name = event.name) }
            }
            is EditPersonEvent.UpdateContact -> {
                _state.update { it.copy(contact = event.contact) }
            }
            is EditPersonEvent.UpdateType -> {
                _state.update { it.copy(selectedType = event.type) }
            }
            is EditPersonEvent.Submit -> {
                submitPerson()
            }
            is EditPersonEvent.ClearError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun loadPerson(person: Person) {
        _state.update {
            it.copy(
                personId = person.personId,
                name = person.name,
                contact = person.contact ?: "",
                selectedType = person.personType
            )
        }
    }

    private fun submitPerson() {
        val currentState = _state.value

        // Prevent duplicate submissions
        if (currentState.isLoading) return

        // Validation
        if (currentState.name.isBlank()) {
            _state.update { it.copy(error = "Name is required") }
            return
        }

        if (currentState.personId.isBlank()) {
            _state.update { it.copy(error = "Invalid person") }
            return
        }

        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val updatedPerson = Person(
                    personId = currentState.personId,
                    name = currentState.name.trim(),
                    contact = currentState.contact.trim().ifBlank { null },
                    personType = currentState.selectedType
                )

                personService.updatePerson(updatedPerson)
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to update person"
                    )
                }
            }
        }
    }
}

