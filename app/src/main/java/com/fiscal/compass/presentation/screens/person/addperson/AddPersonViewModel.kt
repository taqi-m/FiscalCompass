package com.fiscal.compass.presentation.screens.person.addperson

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class AddPersonViewModel @Inject constructor(
    private val personService: PersonService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddPersonScreenState())
    val state: StateFlow<AddPersonScreenState> = _state.asStateFlow()

    init {
        val selectedType = savedStateHandle.get<String>("selectedType") ?: PersonType.CUSTOMER.name
        val availableTypes = PersonType.getDefaultTypes()

        _state.update {
            it.copy(
                selectedType = selectedType,
                availableTypes = availableTypes
            )
        }
    }

    fun onEvent(event: AddPersonEvent) {
        when (event) {
            is AddPersonEvent.UpdateName -> {
                _state.update { it.copy(name = event.name) }
            }
            is AddPersonEvent.UpdateContact -> {
                _state.update { it.copy(contact = event.contact) }
            }
            is AddPersonEvent.UpdateType -> {
                _state.update { it.copy(selectedType = event.type) }
            }
            is AddPersonEvent.Submit -> {
                submitPerson()
            }
            is AddPersonEvent.ClearError -> {
                _state.update { it.copy(error = null) }
            }
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

        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = personService.addPerson(
                name = currentState.name.trim(),
                contact = currentState.contact.trim().ifBlank { null } ?: "",
                personType = currentState.selectedType
            )

            if (result.isSuccess) {
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to add person"
                    )
                }
            }
        }
    }
}

