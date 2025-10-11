package com.app.uniqueplant.presentation.screens.sync

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(): ViewModel(){
    private val _state = MutableStateFlow(SyncScreenState())
    val state: StateFlow<SyncScreenState> = _state.asStateFlow()

    fun onEvent(event: SyncEvent) {
        when (event) {
            is SyncEvent.CancelSync -> {

            }
            is SyncEvent.SyncAll -> {

            }
        }
    }

    private fun updateState(update: SyncScreenState.() -> SyncScreenState) {
        _state.value = _state.value.update()
    }
}