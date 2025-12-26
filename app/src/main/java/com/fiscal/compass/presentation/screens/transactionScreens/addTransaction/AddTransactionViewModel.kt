package com.fiscal.compass.presentation.screens.transactionScreens.addTransaction

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiscal.compass.domain.model.Transaction
import com.fiscal.compass.domain.model.base.Category
import com.fiscal.compass.domain.model.base.Person
import com.fiscal.compass.domain.service.CategoryService
import com.fiscal.compass.domain.service.PersonService
import com.fiscal.compass.domain.service.TransactionService
import com.fiscal.compass.domain.util.TransactionType
import com.fiscal.compass.presentation.screens.category.UiState
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
    private val transactionService: TransactionService,
    private val categoryService: CategoryService,
    private val personService: PersonService
) : ViewModel() {
    val date: Calendar = Calendar.getInstance()
    private val _state = MutableStateFlow(
        AddTransactionState(transaction = Transaction.empty().copy(date = date.time))
    )
    val state: StateFlow<AddTransactionState> = _state.asStateFlow()

    private var expenseCategories: List<Category> = emptyList()
    private var incomeCategories: List<Category> = emptyList()

    private var persons = emptyList<Person>()

    // Store domain models for ItemSelectionScreen
    private var allExpenseCategories = emptyList<Category>()
    private var allIncomeCategories = emptyList<Category>()
    private var allPersons = emptyList<Person>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                allExpenseCategories = categoryService.getExpenseCategories()
                expenseCategories = allExpenseCategories

                allIncomeCategories = categoryService.getIncomeCategories()
                incomeCategories = allIncomeCategories

                allPersons = personService.getAllPersons()
                persons = allPersons



                _state.value = _state.value.copy(
                    allPersons = allPersons
                )
                assignCategories()

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    uiState = UiState.Error(e.message ?: "Unknown error")
                )
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    fun onEvent(event: AddTransactionEvent) {
        when (event) {
            is AddTransactionEvent.OnDescriptionChange -> {
                val updatedTransaction = _state.value.transaction.copy(description = event.description)
                updateState { copy(transaction = updatedTransaction) }
            }
            is AddTransactionEvent.OnResetClicked -> {
                val date = Calendar.getInstance()
                _state.value = AddTransactionState(
                    transaction = Transaction.empty().copy(date = date.time)
                )
            }

            is AddTransactionEvent.OnUiReset -> {
                _state.value = _state.value.copy(
                    uiState = UiState.Idle
                )
            }

            is AddTransactionEvent.OnTypeSelected -> {
                val currentType = _state.value.transaction.transactionType.uppercase()
                val updatedType = event.selectedType.toString().uppercase()
                if (currentType != updatedType) {
                    _state.value = _state.value.copy(
                        transaction = _state.value.transaction.copy(transactionType = updatedType)
                    )
                    assignCategories()
                }
            }

            is AddTransactionEvent.DateSelected -> {
                val currentDate = _state.value.transaction.date
                val updatedCalendar = Calendar.getInstance().apply { time = currentDate }
                updatedCalendar.timeInMillis = event.selectedDate
                val updatedDate = updatedCalendar.time
                val updatedTransaction = _state.value.transaction.copy(date = updatedDate)
                _state.value = _state.value.copy(
                    transaction = updatedTransaction
                )
            }

            is AddTransactionEvent.TimeSelected -> {
                val currentDate = _state.value.transaction.date
                val updatedCalendar = Calendar.getInstance().apply { time = currentDate }
                updatedCalendar.timeInMillis = event.selectedTime.timeInMillis
                val updatedDate = updatedCalendar.time
                val updatedTransaction = _state.value.transaction.copy(date = updatedDate)
                _state.value = _state.value.copy(
                    transaction = updatedTransaction
                )
            }

            AddTransactionEvent.NavigateToCategorySelection -> {
                val transactionType = TransactionType.fromString(_state.value.transaction.transactionType)
                when(transactionType){
                    TransactionType.EXPENSE -> {
                        if (expenseCategories.isEmpty()) {
                            updateState {
                                copy(uiState = UiState.Error("No categories found"))
                            }
                            return
                        }
                    }
                    TransactionType.INCOME -> {
                        if (incomeCategories.isEmpty()) {
                            updateState {
                                copy(uiState = UiState.Error("No categories found"))
                            }
                            return
                        }
                    }
                }
                updateState { copy(navigateToCategorySelection = true) }
            }

            AddTransactionEvent.NavigateToPersonSelection -> {
                updateState { copy(navigateToPersonSelection = true) }
            }

            AddTransactionEvent.NavigateToAmountScreen -> {
                updateState {
                    copy(navigateToAmountScreen = true)
                }
            }

            AddTransactionEvent.ResetNavigation -> {
                updateState {
                    copy(
                        navigateToCategorySelection = false,
                        navigateToPersonSelection = false,
                        navigateToAmountScreen = false
                    )
                }
            }

            is AddTransactionEvent.UpdateSelectedCategory -> {
                val updatedTransaction = _state.value.transaction.copy(categoryId = event.categoryId)
                updateState {
                    copy(
                        transaction = updatedTransaction,
                        navigateToCategorySelection = false
                    )
                }
            }

            is AddTransactionEvent.UpdateSelectedPerson -> {
                val updatedTransaction = _state.value.transaction.copy(personId = event.personId)
                updateState {
                    copy(
                        transaction = updatedTransaction,
                        navigateToPersonSelection = false
                    )
                }
            }

        }
    }

    private fun assignCategories() {
        val transactionType = TransactionType.fromString(_state.value.transaction.transactionType)
        when (transactionType) {
            TransactionType.EXPENSE -> {
                val updatedTransaction = _state.value.transaction.copy(categoryId = 0)
                updateState {
                    copy(
                        allCategories = expenseCategories,
                        transaction = updatedTransaction
                    )
                }
            }

            TransactionType.INCOME -> {
                val updatedTransaction = _state.value.transaction.copy(categoryId = 0)
                updateState {
                    copy(
                        allCategories = incomeCategories,
                        transaction = updatedTransaction
                    )
                }
            }
        }
    }

    private fun updateState(update: AddTransactionState.() -> AddTransactionState) {
        _state.value = _state.value.update()
    }
}
