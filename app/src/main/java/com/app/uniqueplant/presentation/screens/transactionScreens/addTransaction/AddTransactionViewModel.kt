package com.app.uniqueplant.presentation.screens.transactionScreens.addTransaction

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.uniqueplant.domain.usecase.categories.GetCategoriesUseCase
import com.app.uniqueplant.domain.usecase.person.GetAllPersonsUseCase
import com.app.uniqueplant.domain.usecase.transaction.AddTransactionUC
import com.app.uniqueplant.presentation.mappers.formatDate
import com.app.uniqueplant.presentation.mappers.formatTime
import com.app.uniqueplant.presentation.mappers.toGroupedCategoryUi
import com.app.uniqueplant.presentation.mappers.toTransaction
import com.app.uniqueplant.presentation.mappers.toUiList
import com.app.uniqueplant.presentation.model.GroupedCategoryUi
import com.app.uniqueplant.presentation.model.InputField
import com.app.uniqueplant.presentation.model.PersonUi
import com.app.uniqueplant.presentation.model.TransactionType
import com.app.uniqueplant.presentation.model.TransactionUi
import com.app.uniqueplant.presentation.screens.category.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransactionUC: AddTransactionUC,
    private val categoryUseCase: GetCategoriesUseCase,
    private val getPersonUseCase: GetAllPersonsUseCase
) : ViewModel() {
    val date = Calendar.getInstance()
    private val _state = MutableStateFlow(
        AddTransactionState(
            selectedDate = date.time.time,
            selectedTime = date,
            categories = emptyMap(),
            categoryId = 0L,
        )
    )
    val state: StateFlow<AddTransactionState> = _state.asStateFlow()

    private var expenseCategories: GroupedCategoryUi = emptyMap()
    private var incomeCategories: GroupedCategoryUi = emptyMap()

    private var persons = emptyList<PersonUi>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                expenseCategories = categoryUseCase.getExpenseCategoriesTree().toGroupedCategoryUi()

                incomeCategories = categoryUseCase.getIncomeCategoriesTree().toGroupedCategoryUi()

                persons = getPersonUseCase.getAllPersons().toUiList()



                _state.value = _state.value.copy(
                    persons = persons
                )

                Log.d(
                    "AddTransactionViewModel",
                    "init: personId: ${_state.value.personId} persons: ${_state.value.persons} "
                )
                assignCategories()

            } catch (e: Exception) {
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    fun onEvent(event: AddTransactionEvent) {
        when (event) {
            is AddTransactionEvent.OnAmountChange -> {
                _state.value = _state.value.copy(
                    amount = InputField(
                        value = event.amount,
                        error = if (event.amount.isBlank()) "Amount cannot be empty" else ""
                    )
                )
            }

            is AddTransactionEvent.OnDescriptionChange -> {
                _state.value = _state.value.copy(
                    description = InputField(
                        value = event.description,
                        error = if (event.description.isBlank()) "Description cannot be empty" else ""
                    )
                )
            }

            is AddTransactionEvent.OnPersonSelected -> {
                updateState { copy(personId = event.personId) }
            }

            is AddTransactionEvent.OnCategorySelected -> {
                updateState { copy(categoryId = event.categoryId) }
            }

            is AddTransactionEvent.OnSubCategorySelected -> {
                updateState { copy(subCategoryId = event.subCategoryId) }
            }

            is AddTransactionEvent.OnSaveClicked -> {
                addTransaction()
            }

            is AddTransactionEvent.OnResetClicked -> {
                val date = Calendar.getInstance()
                _state.value = AddTransactionState(
                    categories = _state.value.categories,
                    categoryId = _state.value.categoryId,
                    selectedDate = date.timeInMillis,
                    selectedTime = date,
                )
            }

            is AddTransactionEvent.OnUiReset -> {
                _state.value = _state.value.copy(
                    uiState = UiState.Idle
                )
            }

            is AddTransactionEvent.OnTypeSelected -> {
                if (event.selectedType == _state.value.transactionType) {
                } else {
                    _state.value = _state.value.copy(
                        transactionType = event.selectedType
                    )
                    assignCategories()
                }
            }

            is AddTransactionEvent.DateSelected -> {
                updateState {
                    copy(selectedDate = event.selectedDate)
                }
            }

            is AddTransactionEvent.TimeSelected -> {
                updateState {
                    copy(selectedTime = event.selectedTime)
                }
            }

        }
    }

    private fun assignCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            when (_state.value.transactionType) {
                TransactionType.EXPENSE -> {
                    updateState {
                        copy(
                            categories = expenseCategories,
                            categoryId = expenseCategories.keys.first().categoryId,
                        )
                    }
                }

                TransactionType.INCOME -> {
                    updateState {
                        copy(
                            categories = incomeCategories,
                            categoryId = incomeCategories.keys.first().categoryId
                        )
                    }
                }
            }
        }
    }


    private fun addTransaction() {
        // Combine date and time into a single Calendar instance
        val combinedDateTime = Calendar.getInstance().apply {
            _state.value.selectedDate?.let { timeInMillis = it }
            _state.value.selectedTime?.let { time ->
                set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, time.get(Calendar.MINUTE))
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        }

        val transaction = TransactionUi(
            transactionId = 0L,
            formatedAmount = _state.value.amount.value,
            categoryId = _state.value.subCategoryId.takeIf { it != 0L } ?: _state.value.categoryId,
            personId = _state.value.personId,
            formatedTime = formatTime(combinedDateTime.time),
            formatedDate = formatDate(combinedDateTime.time),
            description = _state.value.description.value.ifBlank { null },
            isExpense = _state.value.transactionType == TransactionType.EXPENSE,
            transactionType = _state.value.transactionType.name
        ).toTransaction()

        viewModelScope.launch(Dispatchers.IO) {
            updateState { copy(uiState = UiState.Loading) }
            val result = addTransactionUC(transaction)
            result.onSuccess { transactionId ->
                val newDate = Calendar.getInstance()
                updateState {
                    copy(
                        uiState = UiState.Success("Transaction added successfully"),
                        amount = InputField(""),
                        description = InputField(""),
                        selectedDate = newDate.timeInMillis,
                        selectedTime = newDate
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

    private fun updateState(update: AddTransactionState.() -> AddTransactionState) {
        _state.value = _state.value.update()
    }
}

