package com.app.uniqueplant.presentation.screens.homeScreens.home

import androidx.lifecycle.ViewModel
import com.app.uniqueplant.presentation.navigation.MainScreens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
): ViewModel() {

    private val _state = MutableStateFlow(HomeScreenState())
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()




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
