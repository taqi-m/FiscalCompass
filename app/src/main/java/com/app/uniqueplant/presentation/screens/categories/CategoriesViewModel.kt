package com.app.uniqueplant.presentation.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.uniqueplant.data.rbac.Permission
import com.app.uniqueplant.domain.usecase.categories.AddCategoryUseCase
import com.app.uniqueplant.domain.usecase.categories.DeleteCategoryUseCase
import com.app.uniqueplant.domain.usecase.categories.GetCategoriesUseCase
import com.app.uniqueplant.domain.usecase.categories.UpdateCategoryUseCase
import com.app.uniqueplant.domain.usecase.rbac.CheckPermissionUseCase
import com.app.uniqueplant.presentation.mappers.toCategory
import com.app.uniqueplant.presentation.mappers.toGroupedCategoryUi
import com.app.uniqueplant.presentation.model.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val checkPermissionUseCase: CheckPermissionUseCase
) : ViewModel() {

    private suspend fun checkPermission(permission: Permission): Boolean {
        return checkPermissionUseCase(permission)
    }
    private var categoriesCollectionJob: Job? = null

    private val _state = MutableStateFlow(CategoriesScreenState())
    val state: StateFlow<CategoriesScreenState> = _state.asStateFlow()

    init {
        _state.update { it.copy(uiState = UiState.Loading) }
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(
                canAdd = checkPermission(Permission.ADD_CATEGORY),
                canEdit = checkPermission(Permission.EDIT_CATEGORY),
                canDelete = checkPermission(Permission.DELETE_CATEGORY),
                uiState = UiState.Idle
            )
        }
        updateCategories()
    }

    fun onEvent(event: CategoriesEvent) {
        when (event) {
            is CategoriesEvent.OnUiReset -> {
                updateState {
                    copy(
                        uiState = UiState.Idle,
                        currentDialog = CategoriesDialog.Hidden,
                        dialogState = CategoryDialogState.Idle
                    )
                }
            }

            is CategoriesEvent.OnTransactionTypeChange -> {
                viewModelScope.launch(Dispatchers.Default) {
                    updateState {
                        copy(transactionType = event.transactionType)
                    }
                }
                updateCategories()
            }

            is CategoriesEvent.OnCategoryDialogToggle -> {
                onDialogToggle(event.event)
            }

            is CategoriesEvent.OnCategoryDialogSubmit -> {
                onDialogSubmit(event.event)
                updateState { copy(currentDialog = CategoriesDialog.Hidden) }
            }

            is CategoriesEvent.OnCategoryDialogStateChange -> {
                updateState { copy(dialogState = event.state) }
            }
        }
    }


    private fun onDialogToggle(event: CategoryDialogToggle) {
        viewModelScope.launch {
            when (event) {
                is CategoryDialogToggle.Add -> {
                    if (!checkPermission(Permission.ADD_CATEGORY)) {
                        updateState {
                            copy(
                                uiState = UiState.Error("You don't have permission to add categories")
                            )
                        }
                        return@launch
                    }
                    updateState {
                        copy(
                            currentDialog = CategoriesDialog.AddCategory,
                            dialogState = CategoryDialogState(
                                parentId = event.parentId,
                            )
                        )
                    }
                }

                is CategoryDialogToggle.Edit -> {
                    if (!checkPermission(Permission.EDIT_CATEGORY)) {
                        updateState { copy(
                            uiState = UiState.Error("You don't have permission to edit categories")
                        ) }
                        return@launch
                    }
                    updateState {
                        copy(
                            currentDialog = CategoriesDialog.EditCategory,
                            dialogState = CategoryDialogState(
                                category = event.category
                            )
                        )
                    }
                }

                is CategoryDialogToggle.Delete -> {
                    if (!checkPermission(Permission.DELETE_CATEGORY)) {
                        updateState { copy(
                            uiState = UiState.Error("You don't have permission to delete categories")
                        ) }
                        return@launch
                    }
                    updateState {
                        copy(
                            currentDialog = CategoriesDialog.DeleteCategory,
                            dialogState = CategoryDialogState(
                                category = event.category
                            )
                        )
                    }
                }

                is CategoryDialogToggle.Hidden -> {
                    updateState { copy(currentDialog = CategoriesDialog.Hidden) }
                }
            }
        }
    }


    private fun onDialogSubmit(event: CategoryDialogSubmit) {
        when (event) {
            is CategoryDialogSubmit.Add -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val updatedState = addCategoryUseCase.invoke(
                        name = event.name,
                        parentId = event.parentId,
                        description = event.description,
                        transactionType = _state.value.transactionType,
                        expectedPersonType = event.expectedPersonType
                    )
                    updateState {
                        copy(
                            uiState = updatedState
                        )
                    }
                }
            }

            is CategoryDialogSubmit.Edit -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val result = updateCategoryUseCase.invoke(event.category)
                    if (result.isFailure) {
                        updateState {
                            copy(
                                uiState = UiState.Error(
                                    result.exceptionOrNull()?.message
                                        ?: "An unexpected error occurred"
                                )
                            )
                        }
                        return@launch
                    }
                    updateState {
                        copy(
                            uiState = UiState.Success(
                                result.getOrNull() ?: "Category updated successfully"
                            )
                        )
                    }
                }
            }

            is CategoryDialogSubmit.Delete -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val category = _state.value.dialogState.category ?: return@launch
                    val updatedState = deleteCategoryUseCase.invoke(category.toCategory())
                    updateState {
                        copy(
                            uiState = updatedState
                        )
                    }
                }
            }
        }
    }


    private fun updateCategories() {
        categoriesCollectionJob?.cancel()
        categoriesCollectionJob = viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(uiState = UiState.Loading)
            try {
                val type = _state.value.transactionType
                val currentFlow = when (type) {
                    TransactionType.EXPENSE -> getCategoriesUseCase.getExpenseCategoryTreeFlow()
                    TransactionType.INCOME -> getCategoriesUseCase.getIncomeCategoryTreeFlow()
                }
                currentFlow.collect { updatedCategories ->
                    updateState {
                        copy(
                            uiState = UiState.Idle,
                            categories = updatedCategories.toGroupedCategoryUi()
                        )
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    uiState = UiState.Error(e.message ?: "An unexpected error occurred"),
                )
            }
        }
    }

    private fun updateState(update: CategoriesScreenState.() -> CategoriesScreenState) {
        _state.value = _state.value.update()
    }
}