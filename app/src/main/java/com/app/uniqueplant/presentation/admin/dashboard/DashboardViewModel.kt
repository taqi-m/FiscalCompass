package com.app.uniqueplant.presentation.admin.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.uniqueplant.domain.model.Resource
import com.app.uniqueplant.domain.usecase.LoadDefaultsUseCase
import com.app.uniqueplant.domain.usecase.auth.SessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val sessionUseCase: SessionUseCase,
    private val loadDefaultsUseCase: LoadDefaultsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardScreenState())
    val state: StateFlow<DashboardScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            fetchUserType()
            loadDefaultsUseCase.addDefaultCategories()
        }
    }


    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.LogoutClicked -> {
                logout()
            }

            is DashboardEvent.AddExpenseClicked -> {

            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            sessionUseCase.logout().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        // Handle successful logout, maybe navigate to login screen
                        _state.value = _state.value.copy(isLoggedOut = true)
                    }

                    is Resource.Error -> {
                        // Handle error case, maybe show a message to the user
                        _state.value = _state.value.copy(isLoggedOut = false)
                    }

                    is Resource.Loading -> {
                        // Show loading state if needed
                    }
                }
            }
        }
    }

    private fun fetchUserType() {
        viewModelScope.launch {
            sessionUseCase.getUserType().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _state.value = _state.value.copy(userTypeName = resource.data ?: "")
                    }

                    is Resource.Error -> {
                        // Handle error case, maybe show a message to the user
                        _state.value = _state.value.copy(userTypeName = "Unknown")
                    }

                    is Resource.Loading -> {
                        _state.value = _state.value.copy(userTypeName = "Loading...")
                    }
                }
            }
        }
    }
}