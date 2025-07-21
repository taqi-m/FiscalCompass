package com.app.uniqueplant.presentation.admin.transaction

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.uniqueplant.domain.model.Transaction
import com.app.uniqueplant.domain.usecase.LoadDefaultsUseCase
import com.app.uniqueplant.domain.usecase.transaction.LoadTransactionsUseCase
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

    private val _state = MutableStateFlow(
        TransactionState(
            isLoading = true,
            transactions = transactions,
            selectedTransaction = null,
            isDialogVisible = false,
            isEditMode = false,
            isDeleteMode = false
        )
    )
    val state: StateFlow<TransactionState> = _state.asStateFlow()

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
                loadTransactionsUseCase.loadCurrentMonthTransactions().collect { txs ->
                    _transactions.value = txs
                    _transactions.value = _transactions.value.sortedByDescending { it.date }
                    _state.value = _state.value.copy(
                        isLoading = false,
                        transactions = transactions,
                        selectedTransaction = null
                    )
                }
            } catch (e: Exception) {
                Log.e("TransactionViewModel", "Error loading transactions: ${e.message}")
            }
        }
    
        // Collect current month income
        viewModelScope.launch(Dispatchers.IO) {
            try {
                loadTransactionsUseCase.getCurrentMonthIncome().collect { income ->
                    _state.value = _state.value.copy(
                        incoming = income
                    )
                }
            } catch (e: Exception) {
                Log.e("TransactionViewModel", "Error loading income: ${e.message}")
            }
        }

        // Collect current month expenses
        viewModelScope.launch(Dispatchers.IO) {
            try {
                loadTransactionsUseCase.getCurrentMonthExpense().collect { expense ->
                    _state.value = _state.value.copy(
                        outgoing = expense
                    )
                }
            } catch (e: Exception) {
                Log.e("TransactionViewModel", "Error loading expenses: ${e.message}")
            }
        }
    }

    fun onEvent(event: TransactionEvent) {
        when (event) {
            TransactionEvent.OnDialogToggle -> {
                _state.value = _state.value.copy(
                    isDialogVisible = !_state.value.isDialogVisible
                )
            }

            TransactionEvent.OnResetClicked -> TODO()
            TransactionEvent.OnSaveClicked -> TODO()
            is TransactionEvent.OnTransactionAdded -> TODO()
            is TransactionEvent.OnTransactionDeleted -> {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        loadTransactionsUseCase.deleteTransaction(_state.value.selectedTransaction)
                        _state.value = _state.value.copy(
                            isDeleteMode = false,
                            selectedTransaction = null
                        )
                    } catch (e: Exception) {
                        Log.e("TransactionViewModel", "Error deleting transaction: ${e.message}")
                    }
                }
            }

            is TransactionEvent.OnTransactionEdited -> TODO()
            is TransactionEvent.OnTransactionSelected -> {
                _state.value = _state.value.copy(
                    selectedTransaction = event.transaction,
                )
            }

            TransactionEvent.OnDeleteModeToggle -> {
                _state.value = _state.value.copy(
                    isDeleteMode = !_state.value.isDeleteMode,
                    isEditMode = false
                )
            }

            TransactionEvent.OnEditModeToggle -> {
                _state.value = _state.value.copy(
                    isEditMode = !_state.value.isEditMode,
                    isDeleteMode = false
                )
            }
        }
    }


}