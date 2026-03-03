package com.fiscal.compass.presentation.screens.transactionScreens.transactionDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiscal.compass.domain.model.rbac.Permission
import com.fiscal.compass.domain.model.Transaction
import com.fiscal.compass.domain.service.TransactionService
import com.fiscal.compass.domain.service.analytics.AnalyticsEvent
import com.fiscal.compass.domain.service.analytics.AnalyticsService
import com.fiscal.compass.domain.usecase.rbac.CheckPermissionUseCase
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
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val transactionService: TransactionService,
    private val analyticsService: AnalyticsService
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionDetailsScreenState(uiState = UiState.Loading))
    val state: StateFlow<TransactionDetailsScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val canEdit = checkPermissionUseCase(Permission.EDIT_TRANSACTION)
            val canDelete = checkPermissionUseCase(Permission.DELETE_TRANSACTION)
            updateState { copy(canEdit = canEdit && canDelete) }
        }
    }

    private val coroutineScope = viewModelScope

    fun onEvent(event: TransactionDetailsEvent) {
        when (event) {
            is TransactionDetailsEvent.LoadTransaction -> {
                loadTransaction(event.transaction)
            }
            is TransactionDetailsEvent.ShowDeleteDialog -> {
                updateState { copy(showDeleteDialog = true) }
            }
            is TransactionDetailsEvent.DismissDeleteDialog -> {
                updateState { copy(showDeleteDialog = false) }
            }
            is TransactionDetailsEvent.ConfirmDelete -> {
                deleteTransaction()
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

                // Log transaction viewed event
                analyticsService.logEvent(
                    AnalyticsEvent.TransactionViewed(
                        transactionId = transaction.transactionId,
                        type = if (transaction.isExpense) "expense" else "income"
                    )
                )

                val category = transaction.category?.toUi()
                val person = transaction.person?.toUi()
                updateState { copy(transaction = transaction.toUi(), category = category, person = person, uiState = UiState.Success("Transaction Loaded")) }
            } catch (e: Exception) {
                updateState { copy(uiState = UiState.Error(e.message ?: "Unknown error")) }
            }
        }

    }

    private fun deleteTransaction() {
        val transaction = _state.value.transaction ?: return

        updateState { copy(showDeleteDialog = false, uiState = UiState.Loading) }

        coroutineScope.launch {
            try {
                transactionService.deleteTransaction(
                    transactionId = transaction.transactionId,
                    isExpense = transaction.isExpense
                )
                analyticsService.logEvent(
                    AnalyticsEvent.TransactionDeleted(
                        type = if (transaction.isExpense) "expense" else "income"
                    )
                )
                updateState { copy(uiState = UiState.Success("Transaction deleted successfully")) }
            } catch (e: Exception) {
                updateState { copy(uiState = UiState.Error(e.message ?: "Failed to delete transaction")) }
            }
        }
    }

    private fun updateState(update: TransactionDetailsScreenState.() -> TransactionDetailsScreenState) {
        _state.value = _state.value.update()
    }
}