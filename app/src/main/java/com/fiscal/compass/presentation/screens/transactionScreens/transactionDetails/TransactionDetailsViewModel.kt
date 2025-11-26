package com.fiscal.compass.presentation.screens.transactionScreens.transactionDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiscal.compass.data.mappers.toTransaction
import com.fiscal.compass.domain.model.ExpenseFull
import com.fiscal.compass.domain.model.IncomeFull
import com.fiscal.compass.domain.usecase.expense.GetExpenseWithCategoryAndPerson
import com.fiscal.compass.domain.usecase.income.GetSingleFullIncomeById
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
    private val getSingleFullIncomeById: GetSingleFullIncomeById,
    private val expenseUseCase: GetExpenseWithCategoryAndPerson
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionDetailsScreenState())
    val state: StateFlow<TransactionDetailsScreenState> = _state.asStateFlow()

    private val coroutineScope = viewModelScope

    fun onEvent(event: TransactionDetailsEvent) {
        when (event) {
            is TransactionDetailsEvent.OnScreenLoad -> {
                loadData(event.transactionId, event.isExpense)
            }
        }
    }


    private fun loadData(id: Long, isExpense: Boolean) {
        updateState {
            copy(
                uiState = UiState.Loading
            )
        }

        coroutineScope.launch {
            if (isExpense){
                val data: ExpenseFull? = expenseUseCase(id)
                data?.let {
                    updateState {
                        copy(
                            transaction = it.expense.toTransaction().toUi(),
                            category = it.category?.toUi(),
                            person = it.person?.toUi(),
                            uiState = UiState.Success("")
                        )
                    }
                } ?: run {
                    updateState {
                        copy(
                            uiState = UiState.Error("No data found")
                        )
                    }
                }
            } else {
                val data: IncomeFull? = getSingleFullIncomeById(id)
                data?.let {
                    updateState {
                        copy(
                            transaction = it.income.toTransaction().toUi(),
                            category = it.category?.toUi(),
                            person = it.person?.toUi(),
                            uiState = UiState.Success("")
                        )
                    }
                } ?: run {
                    updateState {
                        copy(
                            uiState = UiState.Error("No data found")
                        )
                    }
                }
            }
        }

    }

    private fun updateState(update: TransactionDetailsScreenState.() -> TransactionDetailsScreenState) {
        _state.value = _state.value.update()
    }
}