package com.app.uniqueplant.presentation.home.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.uniqueplant.domain.model.Resource
import com.app.uniqueplant.domain.usecase.auth.SessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val sessionUseCase: SessionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardScreenState())
    val state: StateFlow<DashboardScreenState> = _state.asStateFlow()

    init {
        fetchUserType()
    }


    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.LogoutClicked -> {
                // Handle logout logic here
                // For example, clear session or navigate to login screen
                sessionUseCase.logout()
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