package com.fiscal.compass.presentation.screens.transactionScreens.addTransaction

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiscal.compass.domain.usecase.categories.GetCategoriesUseCase
import com.fiscal.compass.domain.usecase.person.GetAllPersonsUseCase
import com.fiscal.compass.domain.usecase.transaction.AddTransactionUC
import com.fiscal.compass.presentation.mappers.formatDate
import com.fiscal.compass.presentation.mappers.formatTime
import com.fiscal.compass.presentation.mappers.toTransaction
import com.fiscal.compass.presentation.mappers.toUi
import com.fiscal.compass.presentation.mappers.toUiList
import com.fiscal.compass.presentation.model.CategoryUi
import com.fiscal.compass.presentation.model.InputField
import com.fiscal.compass.presentation.model.PersonUi
import com.fiscal.compass.presentation.model.TransactionType
import com.fiscal.compass.presentation.model.TransactionUi
import com.fiscal.compass.presentation.screens.category.UiState
import com.fiscal.compass.presentation.utils.AmountInputType
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
            categories = emptyList(),
            categoryId = 0L,
        )
    )
    val state: StateFlow<AddTransactionState> = _state.asStateFlow()

    private var expenseCategories: List<CategoryUi> = emptyList()
    private var incomeCategories: List<CategoryUi> = emptyList()

    private var persons = emptyList<PersonUi>()

    // Store domain models for ItemSelectionScreen
    private var allExpenseCategories = emptyList<com.fiscal.compass.domain.model.base.Category>()
    private var allIncomeCategories = emptyList<com.fiscal.compass.domain.model.base.Category>()
    private var allPersons = emptyList<com.fiscal.compass.domain.model.base.Person>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                allExpenseCategories = categoryUseCase.getExpenseCategories()
                expenseCategories = allExpenseCategories.map { it.toUi() }

                allIncomeCategories = categoryUseCase.getIncomeCategories()
                incomeCategories = allIncomeCategories.map { it.toUi() }

                allPersons = getPersonUseCase.getAllPersons()
                persons = allPersons.toUiList()



                _state.value = _state.value.copy(
                    persons = persons,
                    allPersons = allPersons
                )

                Log.d(
                    "AddTransactionViewModel",
                    "init: personId: ${_state.value.personId} persons: ${_state.value.persons} "
                )
                assignCategories()

            } catch (e: Exception) {
                Log.e("AddTransactionViewModel", "init: ", e)
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    fun onEvent(event: AddTransactionEvent) {
        when (event) {
            is AddTransactionEvent.OnAmountChange -> {
                Log.d("AddTransactionViewModel", "onEvent: amount: ${event.amount} inputType: ${event.inputType}")
                when (event.inputType) {
                    AmountInputType.TOTAL_AMOUNT -> {
                        val totalAmount = event.amount.toDoubleOrNull() ?: 0.0
                        val currentPaidAmount = _state.value.paidAmount.value.toDoubleOrNull() ?: 0.0

                        _state.value = _state.value.copy(
                            totalAmount = InputField(
                                value = event.amount,
                                error = when {
                                    event.amount.isBlank() -> "Amount cannot be empty"
                                    totalAmount <= 0 -> "Amount must be greater than zero"
                                    currentPaidAmount > totalAmount -> "Total amount cannot be less than amount paid"
                                    else -> ""
                                }
                            ),
                            // Update amountPaid validation if needed
                            paidAmount = if (currentPaidAmount > totalAmount && totalAmount > 0) {
                                _state.value.paidAmount.copy(
                                    error = "Amount paid cannot exceed total amount"
                                )
                            } else {
                                _state.value.paidAmount.copy(error = "")
                            }
                        )
                    }

                    AmountInputType.AMOUNT_PAID -> {
                        val paidAmount = event.amount.toDoubleOrNull() ?: 0.0
                        val currentTotalAmount = _state.value.totalAmount.value.toDoubleOrNull() ?: 0.0

                        _state.value = _state.value.copy(
                            paidAmount = InputField(
                                value = event.amount,
                                error = when {
                                    event.amount.isBlank() -> "Amount paid cannot be empty"
                                    paidAmount < 0 -> "Amount paid cannot be negative"
                                    paidAmount > currentTotalAmount -> "Amount paid cannot exceed total amount"
                                    else -> ""
                                }
                            )
                        )
                    }
                }
            }

            is AddTransactionEvent.OnAmountPaidChange -> {
                val paidAmount = event.amountPaid.toDoubleOrNull() ?: 0.0
                val currentTotalAmount = _state.value.totalAmount.value.toDoubleOrNull() ?: 0.0

                _state.value = _state.value.copy(
                    paidAmount = InputField(
                        value = event.amountPaid,
                        error = when {
                            event.amountPaid.isBlank() -> "Amount paid cannot be empty"
                            paidAmount < 0 -> "Amount paid cannot be negative"
                            paidAmount > currentTotalAmount -> "Amount paid cannot exceed total amount"
                            else -> ""
                        }
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
                if (event.selectedType != _state.value.transactionType) {
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

            AddTransactionEvent.NavigateToCategorySelection -> {
                updateState { copy(navigateToCategorySelection = true) }
            }

            AddTransactionEvent.NavigateToPersonSelection -> {
                updateState { copy(navigateToPersonSelection = true) }
            }

            AddTransactionEvent.ResetNavigation -> {
                updateState {
                    copy(
                        navigateToCategorySelection = false,
                        navigateToPersonSelection = false
                    )
                }
            }

            is AddTransactionEvent.UpdateSelectedCategory -> {
                updateState {
                    copy(
                        categoryId = event.categoryId,
                        navigateToCategorySelection = false
                    )
                }
            }

            is AddTransactionEvent.UpdateSelectedPerson -> {
                updateState {
                    copy(
                        personId = event.personId,
                        navigateToPersonSelection = false
                    )
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
                            categoryId = expenseCategories.first().categoryId,
                            allCategories = allExpenseCategories
                        )
                    }
                }

                TransactionType.INCOME -> {
                    updateState {
                        copy(
                            categories = incomeCategories,
                            categoryId = incomeCategories.first().categoryId,
                            allCategories = allIncomeCategories
                        )
                    }
                }
            }
        }
    }


    private fun addTransaction() {
        // Validate amountPaid does not exceed amount
        val totalAmount = _state.value.totalAmount.value.toDoubleOrNull() ?: 0.0
        val paidAmount = _state.value.paidAmount.value.toDoubleOrNull() ?: 0.0

        if (paidAmount > totalAmount) {
            updateState {
                copy(uiState = UiState.Error("Amount paid cannot exceed total amount"))
            }
            return
        }

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
            formatedAmount = _state.value.totalAmount.value,
            formatedPaidAmount = _state.value.paidAmount.value,
            categoryId = _state.value.categoryId,
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
            result.onSuccess {
                val newDate = Calendar.getInstance()
                updateState {
                    copy(
                        uiState = UiState.Success("Transaction added successfully"),
                        totalAmount = InputField("0.0"),
                        paidAmount = InputField("0.0"),
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
