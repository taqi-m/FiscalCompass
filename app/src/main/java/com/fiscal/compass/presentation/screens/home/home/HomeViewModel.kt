package com.fiscal.compass.presentation.screens.home.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiscal.compass.domain.model.rbac.Permission
import com.fiscal.compass.domain.service.analytics.AnalyticsEvent
import com.fiscal.compass.domain.service.analytics.AnalyticsService
import com.fiscal.compass.domain.usecase.rbac.CheckPermissionUseCase
import com.fiscal.compass.presentation.screens.category.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val analyticsService: AnalyticsService
): ViewModel() {

    private val _state = MutableStateFlow(HomeScreenState())
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()

    private suspend fun checkPermission(permission: Permission): Boolean {
        return checkPermissionUseCase(permission)
    }

    init {
        _state.update { it.copy(uiState = UiState.Loading) }
        analyticsService.logEvent(AnalyticsEvent.HomeViewed)
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(
                canViewCategories = checkPermission(Permission.VIEW_CATEGORIES),
                canViewPeople = checkPermission(Permission.VIEW_PERSON),
                canAddPerson = checkPermission(Permission.ADD_PERSON),
                canManageUsers = checkPermission(Permission.MANAGE_USERS),
                uiState = UiState.Idle
            )
        }
    }

    fun onEvent(event: HomeEvent) {
        // No events currently — FAB config is derived synchronously in HomeScreen
    }
}
