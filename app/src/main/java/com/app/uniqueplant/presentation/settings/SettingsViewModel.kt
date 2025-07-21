package com.app.uniqueplant.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.uniqueplant.domain.model.Resource
import com.app.uniqueplant.domain.usecase.auth.SessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sessionUseCase: SessionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsScreenState())
    val state: StateFlow<SettingsScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(isLoading = true)
            // Load initial data, such as user info
            sessionUseCase.getUserInfo().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            userInfo = resource.data,
                            isLoading = false,
                            error = null
                        )
                    }

                    is Resource.Error -> {
                        _state.value =
                            _state.value.copy(error = resource.message, isLoading = false)
                    }

                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            sessionUseCase.logout().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        updateState {
                            copy(
                                userInfo = null,
                                error = null,
                                isLogOutSuccess = true
                            )
                        }
                    }

                    is Resource.Error -> {
                        updateState { copy(error = resource.message) }
                    }

                    is Resource.Loading -> {
                        updateState { copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            // Handle events here
            is SettingsEvent.OnLogoutClicked -> {
                logout()
            }
            is SettingsEvent.OnThemeSwitchChanged -> {
                // Handle theme switch change
                updateState { copy(isLoading = true) }
                // Here you would typically call a use case to change the theme
                // For now, we just simulate a delay
                viewModelScope.launch(Dispatchers.IO) {
                    kotlinx.coroutines.delay(1000)
                    updateState { copy(isLoading = false) }
                }
            }

            is SettingsEvent.SampleEventWithParameter -> TODO()
        }
    }


    private fun updateState(update: SettingsScreenState.() -> SettingsScreenState) {
        _state.value = _state.value.update()
    }
}