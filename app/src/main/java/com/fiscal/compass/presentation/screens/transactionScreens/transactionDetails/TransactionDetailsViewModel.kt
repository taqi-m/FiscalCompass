package com.fiscal.compass.presentation.screens.transactionScreens.transactionDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiscal.compass.domain.model.Transaction
import com.fiscal.compass.domain.service.TransactionService
import com.fiscal.compass.presentation.mappers.toUi
import com.fiscal.compass.presentation.screens.category.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionDetailsViewModel @Inject constructor(
    private val transactionService: TransactionService
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionDetailsScreenState(uiState = UiState.Loading))
    val state: StateFlow<TransactionDetailsScreenState> = _state.asStateFlow()

    private val coroutineScope = viewModelScope

    fun onEvent(event: TransactionDetailsEvent) {
        when (event) {
            is TransactionDetailsEvent.LoadTransaction -> {
                loadTransaction(event.transaction)
            }
        }
    }


    private fun loadTransaction(transaction: Transaction?) {
        updateState { copy(uiState = UiState.Loading) }

        coroutineScope.launch {
            try {

                if (transaction == null) {
                    updateState { copy(uiState = UiState.Error("Transaction not found")) }
                    return@launch
                }

                val category = transaction.category?.toUi()
                val person = transaction.person?.toUi()
                updateState { copy(transaction = transaction.toUi(), category = category, person = person, uiState = UiState.Success("Transaction Loaded")) }
            } catch (e: Exception) {
                updateState { copy(uiState = UiState.Error(e.message ?: "Unknown error")) }
            }
        }

    }

    private fun updateState(update: TransactionDetailsScreenState.() -> TransactionDetailsScreenState) {
        _state.value = _state.value.update()
    }
}