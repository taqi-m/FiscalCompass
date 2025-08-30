package com.app.uniqueplant.presentation.screens.addTransaction

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.uniqueplant.domain.model.InputField
import com.app.uniqueplant.domain.usecase.categories.GetCategoriesUseCase
import com.app.uniqueplant.domain.usecase.transaction.AddTransactionUseCase
import com.app.uniqueplant.presentation.mappers.toGroupedCategoryUi
import com.app.uniqueplant.presentation.model.GroupedCategoryUi
import com.app.uniqueplant.presentation.model.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.GregorianCalendar
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val categoryUseCase: GetCategoriesUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(
        AddTransactionState(
            date = Calendar.getInstance().time,
            categories = emptyMap(),
            categoryId = 0L,
        )
    )
    val state: StateFlow<AddTransactionState> = _state.asStateFlow()

    private var expenseCategories: GroupedCategoryUi = emptyMap()
    private var incomeCategories: GroupedCategoryUi = emptyMap()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                expenseCategories = categoryUseCase.getExpenseCategoriesTree().toGroupedCategoryUi()

                incomeCategories = categoryUseCase.getIncomeCategoriesTree().toGroupedCategoryUi()

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

            is AddTransactionEvent.OnDateChange -> {
                _state.value = _state.value.copy(date = event.date)
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
                _state.value = AddTransactionState(
                    categories = _state.value.categories,
                    date = Calendar.getInstance().time,
                    categoryId = _state.value.categoryId,
                )
            }

            is AddTransactionEvent.OnAddTransactionDialogToggle -> {
                onDialogToggle(event.event)
            }

            is AddTransactionEvent.OnAddTransactionDialogSubmit -> {
                onDialogSubmit(event.event)
            }

            is AddTransactionEvent.OnSuccessHandled -> {
                _state.value = _state.value.copy(
                    isSuccess = false,
                    message = ""
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

        }
    }

    private fun onDialogToggle(event: AddTransactionDialog) {
        when (event) {
            AddTransactionDialog.DatePicker -> {
                updateState {
                    copy(currentDialog = AddTransactionDialog.DatePicker)
                }
            }

            AddTransactionDialog.TimePicker -> {
                updateState {
                    copy(currentDialog = AddTransactionDialog.TimePicker)
                }
            }

            AddTransactionDialog.Hidden -> {
                updateState {
                    copy(currentDialog = AddTransactionDialog.Hidden)
                }
            }
        }
    }

    @ExperimentalMaterial3Api
    private fun onDialogSubmit(event: AddTransactionDialogSubmit) {
        when (event) {
            is AddTransactionDialogSubmit.DateSelected -> {
                event.selectedDate?.let { selectedDate ->
                    val calendar = Calendar.getInstance()
                    calendar.time = _state.value.date
                    val selectedCalendar = Calendar.getInstance().apply {
                        timeInMillis = selectedDate
                    }

                    calendar.set(Calendar.YEAR, selectedCalendar.get(Calendar.YEAR))
                    calendar.set(Calendar.MONTH, selectedCalendar.get(Calendar.MONTH))
                    calendar.set(Calendar.DAY_OF_MONTH, selectedCalendar.get(Calendar.DAY_OF_MONTH))

                    updateState {
                        copy(date = calendar.time)
                    }
                }
                updateState {
                    copy(currentDialog = AddTransactionDialog.Hidden)
                }
            }

            is AddTransactionDialogSubmit.TimeSelected -> {

                event.selectedTime?.let { pickedTime ->
                    val calendar = GregorianCalendar.getInstance().apply {
                        time = _state.value.date
                        set(Calendar.HOUR_OF_DAY, pickedTime.hour)
                        set(Calendar.MINUTE, pickedTime.minute)
                    }
                    _state.value = _state.value.copy(date = calendar.time)
                } ?: run {
                }


                /*event.selectedTime?.let { selectedTime ->
                    val calendar = Calendar.getInstance().apply {
                        time = _state.value.date
                        set(Calendar.HOUR_OF_DAY, selectedTime.hour)
                        set(Calendar.MINUTE, selectedTime.minute)
                    }
                    _state.value = _state.value.copy(date = calendar.time)
                }
                _state.value = _state.value.copy(currentDialog = AddTransactionDialog.Hidden)*/
            }

            AddTransactionDialogSubmit.Hidden -> {
                _state.value = _state.value.copy(currentDialog = AddTransactionDialog.Hidden)
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
        viewModelScope.launch(Dispatchers.IO) {
            addTransactionUseCase.addTransaction(
                amount = _state.value.amount.value.toDoubleOrNull() ?: 0.0,
                categoryId = _state.value.subCategoryId ?: _state.value.categoryId,
                description = _state.value.description.value,
                date = _state.value.date,
                transactionType = _state.value.transactionType
            ).onSuccess {
                // Reset state after successful addition
                _state.value = _state.value.copy(
                    isSuccess = true,
                    message = "${_state.value.transactionType.name} added successfully"
                )
            }.onFailure { e ->
                _state.value = _state.value.copy(
                    isSuccess = false,
                    message = "${e.message}"
                )
                // Handle error, e.g., show a message to the user
            }
        }
    }

    private fun updateState(update: AddTransactionState.() -> AddTransactionState) {
        _state.value = _state.value.update()
    }
}

