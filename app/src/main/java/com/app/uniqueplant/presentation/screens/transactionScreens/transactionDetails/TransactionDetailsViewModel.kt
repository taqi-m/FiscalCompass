package com.app.uniqueplant.presentation.screens.transactionScreens.transactionDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.uniqueplant.data.mapper.toTransaction
import com.app.uniqueplant.domain.model.ExpenseFull
import com.app.uniqueplant.domain.model.IncomeFull
import com.app.uniqueplant.domain.usecase.expense.GetExpenseWithCategoryAndPerson
import com.app.uniqueplant.domain.usecase.income.GetSingleFullIncomeById
import com.app.uniqueplant.presentation.mappers.toUi
import com.app.uniqueplant.presentation.screens.categories.UiState
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