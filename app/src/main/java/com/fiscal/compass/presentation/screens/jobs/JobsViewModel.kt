package com.fiscal.compass.presentation.screens.jobs

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class JobsViewModel @Inject constructor(
    // Add your dependencies here
) : ViewModel() {

    private val _state = MutableStateFlow(JobsScreenState())
    val state: StateFlow<JobsScreenState> = _state.asStateFlow()

    fun onEvent(event: JobsEvent) {
        // Handle events here when added
    }

    private fun updateState(update: JobsScreenState.() -> JobsScreenState) {
        _state.value = _state.value.update()
    }
}