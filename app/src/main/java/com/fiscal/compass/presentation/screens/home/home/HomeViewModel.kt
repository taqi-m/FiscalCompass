package com.fiscal.compass.presentation.screens.home.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiscal.compass.data.rbac.Permission
import com.fiscal.compass.domain.usecase.rbac.CheckPermissionUseCase
import com.fiscal.compass.presentation.navigation.MainScreens
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
): ViewModel() {

    private val _state = MutableStateFlow(HomeScreenState())
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()

    private suspend fun checkPermission(permission: Permission): Boolean {
        return checkPermissionUseCase(permission)
    }

    init {
        _state.update { it.copy(uiState = UiState.Loading) }
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(
                canViewCategories = checkPermission(Permission.VIEW_CATEGORIES),
                canViewPeople = checkPermission(Permission.VIEW_PERSON),
                uiState = UiState.Idle
            )
        }
    }

    // Example function to handle logout event
    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.NavigateTo -> { updatedRoute : String ->
                _state.update { it.copy(selectedTab = updatedRoute) }
            }
            is HomeEvent.LogoutClicked -> {
                // Handle logout logic here
            }

            is HomeEvent.ToggleFabExpanded -> {
                _state.update { it.copy(isFabExpanded = !it.isFabExpanded) }
            }

            is HomeEvent.OnScreenLoad -> {
                _state.update { it.copy(appNavController = event.appNavController) }
            }

            is HomeEvent.OnSettingsClicked -> {
                val navController = _state.value.appNavController
                navController?.navigate(MainScreens.Settings.route)
            }

            is HomeEvent.OnSearchClicked -> {
                val navController = _state.value.appNavController
                navController?.navigate(MainScreens.Search.route)
            }
        }
    }
}
