package com.app.uniqueplant.presentation.admin.transaction

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.uniqueplant.domain.repository.CategoryRepository
import com.app.uniqueplant.domain.usecase.transaction.LoadTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val loadTransactionsUseCase: LoadTransactionsUseCase,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _allTransactions = MutableStateFlow<List<Any>>(emptyList())
    val allTransactions: StateFlow<List<Any>> = _allTransactions

    init {
        viewModelScope.launch {
            val categories = categoryRepository.getAllCategories() 
            Log.d(TAG, "Categories: ${categories.joinToString(", ")}")
            try {
                loadTransactionsUseCase.loadAllTransactions().collect { transactions ->
                    _allTransactions.value = transactions
                }
                Log.d(TAG, "Transactions loaded successfully")
                Log.d(TAG, "Transactions: ${_allTransactions.value}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        const val TAG = "TransactionViewModel"
    }

}