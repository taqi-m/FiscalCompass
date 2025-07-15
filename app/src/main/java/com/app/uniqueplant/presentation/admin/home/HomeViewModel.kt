package com.app.uniqueplant.presentation.admin.home

import androidx.lifecycle.ViewModel
import com.app.uniqueplant.domain.usecase.auth.SessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    sessionUseCase: SessionUseCase
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

            HomeEvent.ToggleFabExpanded -> {
                _state.update { it.copy(isFabExpanded = !it.isFabExpanded) }
            }
        }
    }
}
