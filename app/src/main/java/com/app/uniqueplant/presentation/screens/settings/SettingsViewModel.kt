package com.app.uniqueplant.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.uniqueplant.domain.model.dataModels.Resource
import com.app.uniqueplant.domain.usecase.analytics.GetUserInfoUseCase
import com.app.uniqueplant.domain.usecase.auth.SessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val sessionUseCase: SessionUseCase
) : ViewModel() {


    private val _state = MutableStateFlow(SettingsScreenState())
    val state: StateFlow<SettingsScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(isLoading = true)
            // Load initial data, such as user info
            getUserInfoUseCase().let { userInfo ->
                val userInfo = UserInfo(
                    userName = userInfo.userName,
                    userEmail = userInfo.email,
                    profilePictureUrl = userInfo.profilePicUrl
                )
                _state.value = _state.value.copy(userInfo = userInfo, isLoading = false)
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
            is SettingsEvent.OnLogoutClicked -> {
                logout()
            }
        }
    }


    private fun updateState(update: SettingsScreenState.() -> SettingsScreenState) {
        _state.value = _state.value.update()
    }
}