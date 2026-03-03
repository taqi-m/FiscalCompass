package com.fiscal.compass.presentation.screens.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiscal.compass.domain.model.rbac.Permission
import com.fiscal.compass.domain.service.UserService
import com.fiscal.compass.domain.usecase.rbac.CheckPermissionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userService: UserService,
    private val checkPermissionUseCase: CheckPermissionUseCase
): ViewModel(){

    private val _state = MutableStateFlow(UserScreenState())
    val state = _state.asStateFlow()

    private val _canManageUsers = MutableStateFlow(false)
    val canManageUsers: StateFlow<Boolean> = _canManageUsers.asStateFlow()

    init {
        checkManageUsersPermission()
    }

    private fun checkManageUsersPermission() {
        viewModelScope.launch {
            _canManageUsers.value = checkPermissionUseCase(Permission.MANAGE_USERS)
        }
    }

    fun loadUsers() {
        if (_state.value.displayState == null) {
            _state.update {
                it.copy(displayState = DisplayState.Loading)
            }
            fetchUsers()
        }
    }

    fun refreshUsers() {
        _state.update { it.copy(displayState = null) }
        loadUsers()
    }

    fun onEvent(event: UserEvent) {
        when(event) {
            UserEvent.LoadUsers -> loadUsers()
        }
    }


    private fun fetchUsers(){
        viewModelScope.launch {
            try {
                val userResults = UserResults(userService.getAllLocalUsers(), false)
                updateState {
                    copy(
                        displayState = DisplayState.Content(userResults)
                    )
                }
            } catch (e: Exception){
                updateState {
                    copy(
                        displayState = DisplayState.Error
                    )
                }
            }
        }
    }

    private fun onContentDisplayState(block: (content: DisplayState.Content) -> Unit) {
        val displayState = _state.value.displayState
        if (displayState is DisplayState.Content) {
            block(displayState)
        }
    }


    private fun updateState(update: UserScreenState.() -> UserScreenState) {
        _state.value = _state.value.update()
    }
}