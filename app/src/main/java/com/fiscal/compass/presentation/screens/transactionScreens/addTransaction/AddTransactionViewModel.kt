package com.fiscal.compass.presentation.screens.transactionScreens.addTransaction

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiscal.compass.domain.model.Transaction
import com.fiscal.compass.domain.model.base.Category
import com.fiscal.compass.domain.model.base.Person
import com.fiscal.compass.domain.service.CategoryService
import com.fiscal.compass.domain.service.PersonService
import com.fiscal.compass.domain.util.DateTimeUtil
import com.fiscal.compass.domain.util.DateTimeUtil.dateToTimestamp
import com.fiscal.compass.domain.util.DateTimeUtil.getDayOfMonth
import com.fiscal.compass.domain.util.DateTimeUtil.getHourOfDay
import com.fiscal.compass.domain.util.DateTimeUtil.getMinute
import com.fiscal.compass.domain.util.DateTimeUtil.getMonth
import com.fiscal.compass.domain.util.DateTimeUtil.getYear
import com.fiscal.compass.domain.util.DateTimeUtil.setDateOnTimestamp
import com.fiscal.compass.domain.util.DateTimeUtil.setTimeOnTimestamp
import com.fiscal.compass.domain.util.DateTimeUtil.timestampToDate
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
    private val categoryService: CategoryService,
    private val personService: PersonService
) : ViewModel() {
    private val _state = MutableStateFlow(
        AddTransactionState(transaction = Transaction.empty())
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
                val selectedDate = event.selectedDate
                val updatedTimestamp = setDateOnTimestamp(dateToTimestamp(currentDate), getYear(selectedDate), getMonth(selectedDate), getDayOfMonth(selectedDate))
                val updatedTransaction = _state.value.transaction.copy(date = timestampToDate(updatedTimestamp))
                _state.value = _state.value.copy(transaction = updatedTransaction)
            }

            is AddTransactionEvent.TimeSelected -> {
                val currentDate = _state.value.transaction.date
                val selectedTime = event.selectedTime
                val updatedTimestamp = setTimeOnTimestamp(dateToTimestamp(currentDate), getHourOfDay(selectedTime), getMinute(selectedTime))
                Log.d("AddTransactionViewModel", "Previous Time: ${DateTimeUtil.formatDateTime(currentDate)}")
                Log.d("AddTransactionViewModel", "Selected Time: ${DateTimeUtil.formatTimestampAsTime(selectedTime)}")
                Log.d("AddTransactionViewModel", "Updated  Time: ${DateTimeUtil.formatTimestampAsTime(updatedTimestamp)}")
                val updatedTransaction = _state.value.transaction.copy(date = timestampToDate(updatedTimestamp))
                _state.value = _state.value.copy(transaction = updatedTransaction)
            }


            is AddTransactionEvent.UpdateSelectedCategory -> {
                val updatedTransaction = _state.value.transaction.copy(categoryId = event.categoryId)
                updateState {
                    copy(transaction = updatedTransaction)
                }
            }

            is AddTransactionEvent.UpdateSelectedPerson -> {
                val updatedTransaction = _state.value.transaction.copy(personId = event.personId)
                updateState {
                    copy(transaction = updatedTransaction)
                }
            }

        }
    }

    private fun assignCategories() {
        val transactionType = TransactionType.fromString(_state.value.transaction.transactionType)
        when (transactionType) {
            TransactionType.EXPENSE -> {
                val updatedTransaction = _state.value.transaction.copy(categoryId = "0")
                updateState {
                    copy(
                        allCategories = expenseCategories,
                        transaction = updatedTransaction
                    )
                }
            }

            TransactionType.INCOME -> {
                val updatedTransaction = _state.value.transaction.copy(categoryId = "0")
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
