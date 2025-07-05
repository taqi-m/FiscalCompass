package com.app.uniqueplant.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.uniqueplant.domain.model.Resource
import com.app.uniqueplant.domain.usecase.auth.SignUpUseCase
import com.app.uniqueplant.presentation.auth.events.SignUpEvent
import com.app.uniqueplant.presentation.auth.states.SignUpScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
): ViewModel() {

    private val _state = MutableStateFlow(SignUpScreenState())
    val state: StateFlow<SignUpScreenState> = _state

    fun onEvent(event: SignUpEvent) {
        when(event) {
            is SignUpEvent.UsernameChanged -> {
                _state.update { it.copy(username = event.username) }
            }
            is SignUpEvent.EmailChanged -> {
                _state.update { it.copy(email = event.email) }
            }
            is SignUpEvent.PasswordChanged -> {
                _state.update { it.copy(password = event.password) }
            }
            is SignUpEvent.SignUpClicked -> {
                signUp()
            }
            is SignUpEvent.NavigateToLogin -> {
                // Navigation is handled in the UI
            }
            is SignUpEvent.ResetState -> {
                _state.update { SignUpScreenState() }
            }
        }
    }

    private fun signUp() {
        _state.update { it.copy(isLoading = true, error = "") }
        viewModelScope.launch {
            signUpUseCase(_state.value.email, _state.value.password).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        _state.update { it.copy(isLoading = false, isSuccess = true) }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message ?: "Sign up failed"
                            )
                        }
                    }
                }
            }
        }
    }
}