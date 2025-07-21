package com.app.uniqueplant.presentation.admin.transaction

import com.app.uniqueplant.domain.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class TransactionState (
    val isLoading: Boolean = false,
    val error: String? = null,
    val transactions: StateFlow<List<Transaction>> = MutableStateFlow(emptyList()),
    val incoming: Double = 0.0,
    val outgoing: Double = 0.0,
    val selectedTransaction: Any? = null,
    val isDialogVisible: Boolean = false,
    val isEditMode: Boolean = false,
    val isDeleteMode: Boolean = false
)