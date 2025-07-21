package com.app.uniqueplant.presentation.transactions.addExpenseScreen

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.uniqueplant.domain.model.InputField
import com.app.uniqueplant.domain.usecase.AddExpenseUseCase
import com.app.uniqueplant.domain.usecase.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import javax.inject.Inject

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val addExpenseUseCase: AddExpenseUseCase,
    private val categoryUseCase: GetCategoriesUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(
        AddExpenseState(
            date = Calendar.getInstance().time,
            categories = emptyList(),
            categoryId = 0L,
        )
    )
    val state: StateFlow<AddExpenseState> = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val categories = categoryUseCase.getExpenseCategories()
                Log.d(TAG, "Categories fetched successfully: $categories")
                _state.value = _state.value.copy(
                    categories = categories,
                    categoryId = categories.firstOrNull()?.categoryId ?: 0L
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching categories", e)
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    fun onEvent(event: AddExpenseEvent) {
        when (event) {
            is AddExpenseEvent.OnAmountChange -> {
                _state.value = _state.value.copy(
                    amount = InputField(
                        value = event.amount,
                        error = if (event.amount.isBlank()) "Amount cannot be empty" else ""
                    )
                )
            }

            is AddExpenseEvent.OnDescriptionChange -> {
                _state.value = _state.value.copy(
                    description = InputField(
                        value = event.description,
                        error = if (event.description.isBlank()) "Description cannot be empty" else ""
                    )
                )
            }

            is AddExpenseEvent.OnDateChange -> {
                _state.value = _state.value.copy(date = event.date)
            }

            is AddExpenseEvent.OnCategorySelected -> {
                _state.value = _state.value.copy(categoryId = event.categoryId)
                Log.d(TAG, "Category selected: ${event.categoryId}")
            }

            is AddExpenseEvent.OnAccountSelected -> {
                _state.value = _state.value.copy(accountId = event.accountId)
            }

            is AddExpenseEvent.OnSaveClicked -> {
                addExpense()
            }

            is AddExpenseEvent.OnResetClicked -> {
                _state.value = AddExpenseState(
                    categories = _state.value.categories,
                    date = Calendar.getInstance().time,
                    categoryId = _state.value.categoryId,
                )
            }

            is AddExpenseEvent.DateSelected -> {
                event.selectedDate?.let {
                    _state.value = _state.value.copy(date = Date(it))
                    Log.d(TAG, "Date selected: ${_state.value.date}")
                } ?: run {
                    Log.w(TAG, "Selected date is null")
                }
            }

            is AddExpenseEvent.OnDateDialogToggle -> {
                _state.value = _state.value.copy(
                    isDateDialogOpen = !(_state.value.isDateDialogOpen)
                )
            }

            AddExpenseEvent.OnTimeDialogToggle -> {
                _state.value = _state.value.copy(
                    isTimeDialogOpen = !(_state.value.isTimeDialogOpen)
                )
            }

            is AddExpenseEvent.OnTimeSelected -> {
                event.selectedTime?.let { timePickerState ->
                    val calendar = GregorianCalendar.getInstance().apply {
                        time = _state.value.date
                        set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        set(Calendar.MINUTE, timePickerState.minute)
                    }
                    _state.value = _state.value.copy(date = calendar.time)
                    Log.d(TAG, "Time selected: ${_state.value.date}")
                } ?: run {
                    Log.w(TAG, "Selected time is null")
                }
            }

            AddExpenseEvent.OnSuccessHandled -> {
                _state.value = _state.value.copy(
                    isSuccess = false,
                    message = ""
                )
            }
        }
    }

    private fun addExpense() {
        viewModelScope.launch(Dispatchers.IO) {
            addExpenseUseCase.addExpense(
                amount = _state.value.amount.value.toDoubleOrNull() ?: 0.0,
                categoryId = _state.value.categoryId,
                description = _state.value.description.value,
                date = _state.value.date
            ).onSuccess {
                Log.d(TAG, "Expense added successfully")
                // Reset state after successful addition
                onEvent(AddExpenseEvent.OnResetClicked)
                _state.value = _state.value.copy(
                    isSuccess = true,
                    message = "Expense added successfully"
                )
                Log.d(TAG, "State after adding expense: ${_state.value}")
            }.onFailure { e ->
                Log.e(TAG, "Error adding expense", e)
                // Handle error, e.g., show a message to the user
            }
        }

    }


    companion object {
        private const val TAG = "AddIncomeViewModel"
    }
}

