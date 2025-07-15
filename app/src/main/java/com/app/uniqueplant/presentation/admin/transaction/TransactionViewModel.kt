package com.app.uniqueplant.presentation.admin.transaction

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.uniqueplant.data.model.Expense
import com.app.uniqueplant.data.model.Income
import com.app.uniqueplant.domain.repository.CategoryRepository
import com.app.uniqueplant.domain.usecase.transaction.LoadTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val loadTransactionsUseCase: LoadTransactionsUseCase,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Any>>(emptyList())
    val transactions: StateFlow<List<Any>> = _transactions.asStateFlow()

    private val _state = MutableStateFlow(TransactionState(
        isLoading = true,
        transactions = transactions,
        selectedTransaction = null,
        isDialogVisible = false,
        isEditMode = false,
        isDeleteMode = false
    ))
    val state: StateFlow<TransactionState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                loadTransactionsUseCase.loadAllTransactions().collect { txs ->
                    _transactions.value = txs
                    _state.value = _state.value.copy(
                        isLoading = false,
                        transactions = transactions,
                        incoming = txs.filterIsInstance<Income>().sumOf { it.amount },
                        outgoing = txs.filterIsInstance<Expense>().sumOf { it.amount }
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
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