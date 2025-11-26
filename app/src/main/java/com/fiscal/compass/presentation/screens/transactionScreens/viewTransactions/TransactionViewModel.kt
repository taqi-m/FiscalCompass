package com.fiscal.compass.presentation.screens.transactionScreens.viewTransactions

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiscal.compass.domain.usecase.transaction.LoadTransactionsUC
import com.fiscal.compass.presentation.mappers.toUi
import com.fiscal.compass.presentation.screens.category.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val loadTransactionsUC: LoadTransactionsUC,
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionScreenState(
            uiState = UiState.Loading,
            currentDate = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }.time,
        ))
    val state: StateFlow<TransactionScreenState> = _state.asStateFlow()

    init {
        loadTransactionsData()
    }

    fun onEvent(event: TransactionEvent) {
        when (event) {
            is TransactionEvent.OnTransactionDialogToggle -> {
                onDialogToggle(event.event)
            }

            is TransactionEvent.OnPreviousMonth ->{
                updateState {
                    val calendar = Calendar.getInstance().apply {
                        time = currentDate
                        add(Calendar.MONTH, -1)
                    }
                    copy(
                        currentDate = calendar.time,
                        uiState = UiState.Loading,
                        transactions = emptyMap(),
                    )
                }
                loadTransactionsData()
            }

            is TransactionEvent.OnNextMonth ->{
                updateState {
                    val calendar = Calendar.getInstance().apply {
                        time = currentDate
                        add(Calendar.MONTH, 1)
                    }
                    copy(
                        currentDate = calendar.time,
                        uiState = UiState.Loading,
                        transactions = emptyMap(),
                    )
                }
                loadTransactionsData()
            }

            is TransactionEvent.OnTransactionSelected -> {
                navigateToTransactionDetails@ for (transactionsOnDate in _state.value.transactions.values) {
                    for (transaction in transactionsOnDate) {
                        if (transaction.transactionId == event.transaction.transactionId && transaction.isExpense == event.transaction.isExpense) {
                            // Found the transaction, proceed with navigation logic
                            Log.d("TransactionViewModel", "Navigating to details of transaction ID: ${transaction.transactionId}")
                            break@navigateToTransactionDetails
                        }
                    }
                }
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
                        loadTransactionsUC.deleteTransaction(transaction.transactionId, transaction.isExpense)
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


    private fun loadTransactionsData() {
        viewModelScope.launch(Dispatchers.IO) {
            // Combine the three flows into a single flow that emits a Triple
            val combinedFlow = combine(
                loadTransactionsUC.currentMonthTransactions(_state.value.currentDate),
                loadTransactionsUC.getCurrentMonthIncome(_state.value.currentDate),
                loadTransactionsUC.getCurrentMonthExpense(_state.value.currentDate)
            ) { transactions, income, expense ->
                // This lambda is called when any of the flows emit a new value.
                // It provides the latest value from each.
                Triple(transactions, income, expense)
            }

            // Collect the combined results
            combinedFlow
                .catch { e ->
                    // Handle exceptions from any of the upstream flows in one place
                    Log.e("TransactionViewModel", "Error loading transaction data: ${e.message}")
                    updateState { copy(uiState = UiState.Error("Error loading transaction data")) }
                }
                .collect { (transactions, income, expense) ->
                    // Update the state once with all the new data
                    updateState {
                        copy(
                            transactions = transactions.mapValues { entry ->
                                entry.value.map { it.toUi() }
                            },
                            incoming = income,
                            outgoing = expense,
                            currentDialog = TransactionScreenDialog.Hidden,
                            dialogState = TransactionDialogState.Idle,
                            uiState = UiState.Idle // Set Idle state here
                        )
                    }
                }
        }
    }

    private fun updateState(update: TransactionScreenState.() -> TransactionScreenState) {
        _state.value = _state.value.update()
    }


}