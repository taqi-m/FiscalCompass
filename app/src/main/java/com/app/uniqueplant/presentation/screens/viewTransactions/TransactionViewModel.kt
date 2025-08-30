package com.app.uniqueplant.presentation.screens.viewTransactions

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.uniqueplant.domain.model.Transaction
import com.app.uniqueplant.domain.usecase.LoadDefaultsUseCase
import com.app.uniqueplant.domain.usecase.transaction.LoadTransactionsUseCase
import com.app.uniqueplant.presentation.mappers.toTransactionUi
import com.app.uniqueplant.presentation.screens.categories.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val loadTransactionsUseCase: LoadTransactionsUseCase,
    private val loadDefaultsUseCase: LoadDefaultsUseCase,
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _state = MutableStateFlow(TransactionScreenState(
            uiState = UiState.Loading,
        ))
    val state: StateFlow<TransactionScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                loadDefaultsUseCase.addDefaultCategories()
            } catch (e: Exception) {
                Log.e("TransactionViewModel", "Error loading defaults: ${e.message}")
            }
        }

        // Collect all transactions
        viewModelScope.launch(Dispatchers.IO) {
            try {
                loadTransactionsUseCase.loadCurrentMonthTransactions().collect { transactions ->
                    updateState {
                        copy(
                            uiState = UiState.Idle,
                            transactions = transactions.mapValues { entry ->
                                entry.value.map { it.toTransactionUi() }
                            },
                            currentDialog = TransactionScreenDialog.Hidden,
                            dialogState = TransactionDialogState.Idle
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("TransactionViewModel", "Error loading transactions: ${e.message}")
            }
        }

        // Collect current month income
        viewModelScope.launch(Dispatchers.IO) {
            try {
                loadTransactionsUseCase.getCurrentMonthIncome().collect { income ->
                    updateState {
                        copy(
                            incoming = income
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("TransactionViewModel", "Error loading income: ${e.message}")
            }
        }

        // Collect current month expenses
        viewModelScope.launch(Dispatchers.IO) {
            try {
                loadTransactionsUseCase.getCurrentMonthExpense().collect { expense ->
                    updateState {
                        copy(
                            outgoing = expense
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("TransactionViewModel", "Error loading expenses: ${e.message}")
            }
        }
    }

    fun onEvent(event: TransactionEvent) {
        when (event) {
            is TransactionEvent.OnTransactionDialogToggle -> {
                onDialogToggle(event.event)
            }

            is TransactionEvent.OnTransactionDialogSubmit -> {
                onDialogSubmit(event.event)
            }

            is TransactionEvent.OnTransactionDialogStateChange -> {
                updateState { copy(dialogState = event.state) }
            }

            TransactionEvent.OnUiReset -> {
                updateState {
                    copy(
                        uiState = UiState.Idle,
                        currentDialog = TransactionScreenDialog.Hidden,
                        dialogState = TransactionDialogState.Idle,
                    )
                }
            }
        }
    }

    private fun onDialogToggle(currentDialog: TransactionDialogToggle) {
        when (currentDialog) {
            is TransactionDialogToggle.Edit -> {
                updateState {
                    copy(
                        currentDialog = TransactionScreenDialog.EditTransaction,
                        dialogState = TransactionDialogState(transaction = currentDialog.transaction)
                    )
                }
            }

            is TransactionDialogToggle.Delete -> {
                updateState {
                    copy(
                        currentDialog = TransactionScreenDialog.DeleteTransaction,
                        dialogState = TransactionDialogState(transaction = currentDialog.transaction)
                    )
                }
            }

            TransactionDialogToggle.Hidden -> {
                updateState {
                    copy(
                        currentDialog = TransactionScreenDialog.Hidden,
                        dialogState = TransactionDialogState.Idle
                    )
                }
            }
        }
    }

    private fun onDialogSubmit(event: TransactionDialogSubmit) {
        when (event) {
            is TransactionDialogSubmit.Edit -> TODO()
            is TransactionDialogSubmit.Delete -> {
                val transaction = _state.value.dialogState.transaction
                if (transaction == null) {
                    updateState {
                        copy(
                            uiState = UiState.Error("Transaction not found"),
                            currentDialog = TransactionScreenDialog.Hidden,
                            dialogState = TransactionDialogState.Idle
                        )
                    }
                    return
                }
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        loadTransactionsUseCase.deleteTransaction(transaction.transactionId, transaction.isExpense)
                        updateState {
                            copy(uiState = UiState.Success("Transaction deleted successfully"), currentDialog = TransactionScreenDialog.Hidden, dialogState = TransactionDialogState.Idle)
                        }
                    } catch (e: Exception) {
                        updateState { copy(uiState = UiState.Error("Unknown Error Occurred!")) }
                        Log.e("TransactionViewModel", "Error deleting transaction: ${e.message}")
                    }
                }
            }
        }

    }


    private fun updateState(update: TransactionScreenState.() -> TransactionScreenState) {
        _state.value = _state.value.update()
    }


}