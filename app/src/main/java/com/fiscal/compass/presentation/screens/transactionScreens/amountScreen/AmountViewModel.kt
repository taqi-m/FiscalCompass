package com.fiscal.compass.presentation.screens.transactionScreens.amountScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiscal.compass.domain.model.Transaction
import com.fiscal.compass.domain.service.TransactionService
import com.fiscal.compass.presentation.screens.category.UiState
import com.fiscal.compass.presentation.utils.AmountInputType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AmountViewModel @Inject constructor(
    private val transactionService: TransactionService
) : ViewModel() {

    private val _state = MutableStateFlow(
        AmountScreenState()
    )
    val state: StateFlow<AmountScreenState> = _state.asStateFlow()

    fun onEvent(event: AmountEvent) {
        when (event) {
            is AmountEvent.LoadTransaction -> {
                loadTransactionDetails(event.transaction, event.editMode)
            }

            is AmountEvent.OnAmountChange -> {
                when (event.inputType) {
                    AmountInputType.TOTAL_AMOUNT -> {
                        val totalAmount = event.amount
                        _state.value = _state.value.copy(
                            totalAmount = totalAmount
                        )
                    }

                    AmountInputType.AMOUNT_PAID -> {
                        val paidAmount = event.amount

                        _state.value = _state.value.copy(paidAmount = paidAmount)
                    }
                }
            }

            is AmountEvent.OnAmountPaidChange -> {
                updateState { copy(paidAmount = event.amountPaid) }
            }

            AmountEvent.OnSaveClicked -> {
                updateTransaction()
            }
        }
    }


    private fun loadTransactionDetails(transaction: Transaction, editMode: Boolean) {
        val totalAmount = transaction.amount
        val paidAmount = transaction.amountPaid
        
        updateState {
            copy(
                editMode= editMode,
                transaction = transaction,
                totalAmount = totalAmount,
                paidAmount = paidAmount
            )
        }
    }

    private fun updateTransaction() {
        // Validate amountPaid does not exceed amount
        val totalAmount = _state.value.totalAmount
        val paidAmount = _state.value.paidAmount

        if (paidAmount > totalAmount) {
            updateState { copy(uiState = UiState.Error("Amount paid cannot exceed total amount")) }
            return
        }

        val existingTransaction = state.value.transaction
        // Update both amount and amountPaid - in edit mode, total amount should also be updatable
        val transaction = existingTransaction.copy(
            amount = totalAmount,
            amountPaid = paidAmount
        )

        viewModelScope.launch(Dispatchers.IO) {
            updateState { copy(uiState = UiState.Loading) }
            val result = if (state.value.editMode) {
                transactionService.updateTransaction(transaction)
            } else {
                transactionService.addTransaction(transaction)
            }

            val successMessage = if (state.value.editMode) {
                "Transaction updated successfully"
            } else {
                "Transaction added successfully"
            }
            result.onSuccess {
                updateState {
                    copy(
                        uiState = UiState.Success(successMessage),
                        totalAmount = 0.0,
                        paidAmount = 0.0,
                    )
                }
            }.onFailure { exception ->
                updateState {
                    copy(
                        uiState = UiState.Error(exception.message ?: "An error occurred")
                    )
                }
            }
        }
    }

    private fun updateState(update: AmountScreenState.() -> AmountScreenState) {
        _state.value = _state.value.update()
    }
}