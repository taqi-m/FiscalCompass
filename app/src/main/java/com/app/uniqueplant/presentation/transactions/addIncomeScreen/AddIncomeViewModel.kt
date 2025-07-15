package com.app.uniqueplant.presentation.transactions.addIncomeScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.uniqueplant.domain.usecase.AddIncomeUseCase
import com.app.uniqueplant.domain.usecase.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddIncomeViewModel @Inject constructor(
    private val addIncomeUseCase: AddIncomeUseCase,
    private val categoryUseCase: GetCategoriesUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(AddIncomeState(
        date = Date(System.currentTimeMillis()),
        categories = emptyList()
    ))
    val state: StateFlow<AddIncomeState> = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val categories = categoryUseCase.getIncomeCategories()
                Log.d(TAG, "Categories fetched successfully: $categories")
                _state.value = _state.value.copy(categories = categories)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching categories", e)
            }
        }
    }


    fun onEvent(event: AddIncomeEvent) {
        when (event) {
            is AddIncomeEvent.OnAmountChange -> {
                _state.value = _state.value.copy(amount = InputField(
                    value = event.amount,
                    error = if (event.amount.isBlank()) "Amount cannot be empty" else ""
                ))
            }

            is AddIncomeEvent.OnDescriptionChange -> {
                _state.value = _state.value.copy(description = InputField(
                    value = event.description,
                    error = if (event.description.isBlank()) "Description cannot be empty" else ""
                ))
            }

            is AddIncomeEvent.OnDateChange -> {
                _state.value = _state.value.copy(date = event.date)
            }

            is AddIncomeEvent.OnCategorySelected -> {
                _state.value = _state.value.copy(categoryId = event.categoryId)
                Log.d(TAG, "Category selected: ${event.categoryId}")
            }

            is AddIncomeEvent.OnAccountSelected -> {
                _state.value = _state.value.copy(accountId = event.accountId)
            }

            is AddIncomeEvent.OnSaveClicked -> {
                addIncome()
            }

            is AddIncomeEvent.OnResetClicked -> {
                _state.value = AddIncomeState(
                    categories = _state.value.categories,
                    date = Date(System.currentTimeMillis() / 1000L),
                )
            }

            is AddIncomeEvent.DateSelected -> {
                event.selectedDate?.let {
                    _state.value = _state.value.copy(date = Date(it))
                    Log.d(TAG, "Date selected: ${_state.value.date}")
                } ?: run {
                    Log.w(TAG, "Selected date is null")
                }
            }

            is AddIncomeEvent.OnDialogDismiss -> {
                _state.value = _state.value.copy(
                    isDialogOpen = !(_state.value.isDialogOpen)
                )
            }
        }
    }

    private fun addIncome() {
        viewModelScope.launch(Dispatchers.IO) {
            addIncomeUseCase.addIncome(
                amount = _state.value.amount.value.toDoubleOrNull() ?: 0.0,
                categoryId = _state.value.categoryId,
                description = _state.value.description.value,
                date = _state.value.date
            ).onSuccess {
                Log.d(TAG, "Income added successfully")
                // Reset state after successful addition
                onEvent(AddIncomeEvent.OnResetClicked)
            }.onFailure { e ->
                Log.e(TAG, "Error adding income", e)
                // Handle error, e.g., show a message to the user
            }
        }

    }



    companion object {
        private const val TAG = "AddIncomeViewModel"
    }
}

